using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace HTTPServerService
{
    internal class WebRequest
    {
        private Socket _socket;
        private StreamReader _stream;
        private RequestParams _requestParams;
        private string _rootDirectory = string.Empty;
        private List<string> _mimeTypeList;

        public string RootDirectory
        {
            get { return _rootDirectory; }
            set
            {
                if (Directory.Exists(value))
                {
                    _rootDirectory = value.EndsWith(@"\") ? value : value + @"\";
                }
            }
        }

        public WebRequest(Socket socket)
        {
            _socket = socket;
            _stream = new StreamReader(new NetworkStream(_socket));
            _requestParams = new RequestParams();
            _mimeTypeList = new List<string>()
            {
                ".html", ".htm", ".txt", ".mpeg", ".mpg", ".mpe", ".avi", ".png", "jpeg", ".jpg", ".jpe", ".gif",".bmp", ".wav",
                ".mp3", ".mp2", "aif", ".aiff", ".tiff", ".tif"
            };
        }

        public void StartProcessing()
        {
            GetRequest();
            SendResponse();
        }

        private void GetRequest()
        {
            try
            {
                _requestParams.IsValid = (_stream != null && !_stream.EndOfStream);

                bool parse = _requestParams.IsValid;
                while (parse)
                {
                    parse = ParseRequest(_stream.ReadLine());
                }
            }
            catch (Exception)
            {
                _requestParams.IsValid = false;
            }
        }

        private void SendResponse()
        {
            // Response variables
            string response = string.Empty;
            byte[] fileBytes = new byte[0];
            byte[] sendBytes;

            if (!_requestParams.IsValid)
            {
                // Send 400 bad redquest
                _requestParams.StatusCode = StatusCodeType.BadRequest;
                _requestParams.MimeType = MIMEType.HTML;
                response = _requestParams.HeaderToString();
                response += _requestParams.ErrorCodeHtml();

                sendBytes = Encoding.UTF8.GetBytes(response);
                _socket.Send(sendBytes);

                return;
            }

            // Local file
            // Remove leading / and replace the remaining / to line up
            // with Windows.
            string windowsPath = _requestParams.ServerDirectory.Replace("/", @"\");

            // Initial request state
            if (windowsPath.EndsWith(@"\"))
            {
                windowsPath += "index.html";
            }

            // Remove the first slash to later append to root dir
            windowsPath = windowsPath.Remove(0, 1);

            _requestParams.FullFilePath = Path.Combine(_rootDirectory, windowsPath);

            if (File.Exists(_requestParams.FullFilePath))
            {
                try
                {
                    ReadFileStream(ref response, ref fileBytes);
                }
                catch (Exception)
                {
                    // Send 500 internal server error
                    _requestParams.StatusCode = StatusCodeType.InternalServerError;
                    _requestParams.MimeType = MIMEType.HTML;
                    response = _requestParams.HeaderToString();
                    response += _requestParams.ErrorCodeHtml();
                    _requestParams.IsValid = false;
                }
            }
            else
            {
                // Send 404 Not Found
                _requestParams.StatusCode = StatusCodeType.FileNotFound;
                _requestParams.MimeType = MIMEType.HTML;
                response = _requestParams.HeaderToString();
                response += _requestParams.ErrorCodeHtml();
                _requestParams.IsValid = false;
            }

            if (_requestParams.IsValid &&
                (_requestParams.MimeType == MIMEType.JPEG ||
                _requestParams.MimeType == MIMEType.PNG ||
                _requestParams.MimeType == MIMEType.BMP ||
                _requestParams.MimeType == MIMEType.GIF ||
                _requestParams.MimeType == MIMEType.TIFF ||
                _requestParams.MimeType == MIMEType.MP3 ||
                _requestParams.MimeType == MIMEType.MPEG ||
                _requestParams.MimeType == MIMEType.WAV ||
                _requestParams.MimeType == MIMEType.AVI ||
                _requestParams.MimeType == MIMEType.AIFF))
            {
                // Send inline file
                byte[] header = Encoding.UTF8.GetBytes(response);
                sendBytes = new byte[header.Length + fileBytes.Length];
                Array.Copy(header, sendBytes, header.Length);
                Array.Copy(fileBytes, 0, sendBytes, header.Length, fileBytes.Length);
            }
            else
            {
                sendBytes = Encoding.UTF8.GetBytes(response);
            }

            _socket.Send(sendBytes);

        }

        private bool ParseRequest(string line)
        {
            if (line.Equals(string.Empty))
            {
                return false;
            }

            if (line.StartsWith("GET"))
            {
                string[] getParams = line.Split(' ');

                if (getParams.Length != 3)
                {
                    return _requestParams.IsValid = false;
                }

                _requestParams.IsValid = true;

                string fileOrDir = getParams[1];
                if (fileOrDir.EndsWith("/"))
                {
                    // Initial index request
                    _requestParams.MimeType = MIMEType.HTML;
                }
                else if (fileOrDir.Contains("."))
                {
                    string extension = fileOrDir.Substring(fileOrDir.LastIndexOf("."));

                    if (_mimeTypeList.Contains(extension))
                    {
                        // Get response file type
                        _requestParams.SetMimeType(extension);
                    }
                    else
                    {
                        // The "." is a directory or incompatible file type
                        if (Directory.Exists(Path.Combine(_rootDirectory, fileOrDir.Remove(0, 1).Replace("/", @"\"))))
                        {
                            fileOrDir = fileOrDir + "/";
                            _requestParams.MimeType = MIMEType.HTML;
                        }
                    }
                }
                else
                {
                    // This is a directory
                    fileOrDir = fileOrDir + "/";
                    _requestParams.MimeType = MIMEType.HTML;
                }

                _requestParams.ServerDirectory = fileOrDir;
                _requestParams.HTTP1 = getParams[2].StartsWith("HTTP/1");
            }

            return true;
        }

        private void ReadFileStream(ref string response, ref byte[] fileBuffer)
        {
            // Read the file stream
            using (FileStream fileReader = File.OpenRead(_requestParams.FullFilePath))
            {
                response = _requestParams.HeaderToString() +
                           _requestParams.ContentTypeToString();

                if (_requestParams.MimeType == MIMEType.HTML ||
                        _requestParams.MimeType == MIMEType.PLAIN ||
                        _requestParams.MimeType == MIMEType.CSS)
                {
                    StreamReader sr = new StreamReader(fileReader);
                    string fileContents = sr.ReadToEnd();
                    response += _requestParams.ContentLengthToString(fileContents.Length)
                        + "\r\n" + fileContents;
                }
                else
                {
                    FileInfo fi = new FileInfo(_requestParams.FullFilePath);
                    BinaryReader br = new BinaryReader(fileReader);
                    fileBuffer = br.ReadBytes((int)fi.Length);
                    fileReader.Close();

                    response += _requestParams.ContentLengthToString(fileBuffer.Length) + "\r\n";
                }
            }
        }
    }
}
