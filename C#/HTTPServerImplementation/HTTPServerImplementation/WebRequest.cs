using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace HTTPServerImplementation
{
    internal class WebRequest
    {
        private Socket _socket;
        private StreamReader _stream;
        private RequestParams _requestParams;
        private string _rootDirectory;

        public string RootDirectory
        {
            get { return _rootDirectory; }
            set
            {
                if (Directory.Exists(value))
                {
                    _rootDirectory = value.EndsWith(@"\") ? value : value + @"\";
                }
                else
                {
                    _rootDirectory = string.Empty;
                }
            }
        }

        public WebRequest(Socket socket)
        {
            _socket = socket;
            _stream = new StreamReader(new NetworkStream(_socket));
            _requestParams = new RequestParams();
        }

        public void StartProcessing()
        {
            GetRequest();
            SendResponse();
        }

        private void GetRequest()
        {
            _requestParams.IsValid = (_stream != null && !_stream.EndOfStream);
            
            bool parse = true;
            while (parse)
            {
                parse = ParseRequest(_stream.ReadLine());
            }
        }

        private void SendResponse()
        {
            // Create a response
            DateTime utc = DateTime.UtcNow;
            string response;

            if (_requestParams.IsValid)
            {
                string windowsPath;

                // Initial request state
                if (_requestParams.ServerDirectory.Equals("/"))
                {
                    windowsPath = "index.html";
                }
                else
                {
                    windowsPath = _requestParams.ServerDirectory.Remove(0, 1).Replace("/", @"\");
                }

                string fullPath = Path.Combine(_rootDirectory, windowsPath);

                if (File.Exists(fullPath))
                {
                    // Get the MIME content type
                    string contentType;

                    switch (_requestParams.MimeType)
                    {
                        case MIMEType.HTML:
                            contentType = "text/html";
                            break;
                        case MIMEType.JPEG:
                            contentType = "image/jpeg";
                            break;
                        case MIMEType.MP3:
                            contentType = "audio/mpeg";
                            break;
                        case MIMEType.PLAIN:
                            contentType = "text/plain";
                            break;
                        default:
                            throw new ArgumentOutOfRangeException();
                    }

                    // Read the file stream
                    using (FileStream fileReader = File.OpenRead(fullPath))
                    {
                        response = "HTTP/ 1.1 200 OK\r\n" +
                                   $"Date: {utc.ToString("R")}\r\n" +
                                   "Server: localhostServer/2.1\r\n" +
                                   $"Content-Type: {contentType}\r\n";

                        if (_requestParams.MimeType == MIMEType.HTML || _requestParams.MimeType == MIMEType.PLAIN)
                        {
                            StreamReader sr = new StreamReader(fileReader);
                            string fileContents = sr.ReadToEnd();

                            response = response + $"Content-Length: {fileContents.Length}\r\n\r\n" + fileContents;
                        }
                        else
                        {
                            
                        }
                    }
                }
                else
                {
                    // Respond with the stuff needed for a failed get file repsonse code
                    response = string.Empty;
                }
            }
            else
            {
                //Send 400 fail request
                response = "HTTP/ 1.1 400 OK\r\n" +
                    string.Format("Date: {0}\r\n", utc.ToString("R")) +
                    "Server: localhostServer/2.1\r\n";
            }

            byte[] sendBytes = Encoding.UTF8.GetBytes(response);
            _socket.Send(sendBytes);
        }

        private bool ParseRequest(string line)
        {
            if (line.Equals(string.Empty))
            {
                return false;
            }

            // Continue parsing
            Console.WriteLine(line);

            if (line.StartsWith("GET"))
            {
                string[] getParams = line.Split(' ');

                if (getParams.Length == 3)
                {
                    _requestParams.IsValid = true;
                    _requestParams.ServerDirectory = getParams[1];

                    if (getParams[1].Equals("/"))
                    {
                        // Initial index request
                        _requestParams.MimeType = MIMEType.HTML;
                    }
                    else
                    {
                        MIMEType type = MIMEType.PLAIN; // Default case

                        if (getParams[1].EndsWith(".html") || getParams[1].EndsWith(".htm"))
                        {
                            type = MIMEType.HTML;
                        }
                        else if (getParams[1].EndsWith(".jpeg") || getParams[1].EndsWith(".jpg"))
                        {
                            type = MIMEType.JPEG;
                        }
                        else if (getParams[1].EndsWith(".txt"))
                        {
                            type = MIMEType.PLAIN;
                        }
                        else if (getParams[1].EndsWith(".mp3"))
                        {
                            type = MIMEType.MP3;
                        }

                        _requestParams.MimeType = type;
                    }

                    _requestParams.HTTP11 = getParams[2] == "HTTP/1.1";
                }
                else
                {
                    _requestParams.IsValid = false;
                }
            }

            return true;
        }
    }
}
