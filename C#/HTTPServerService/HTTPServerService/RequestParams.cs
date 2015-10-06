using System;

namespace HTTPServerService
{
    internal class RequestParams
    {
        public bool IsValid { get; set; } = false;
        public bool HTTP1 { get; set; } = false;

        public MIMEType MimeType { get; set; } = MIMEType.PLAIN;
        public StatusCodeType StatusCode { get; set; } = StatusCodeType.OK;

        public string ServerDirectory { get; set; }
        public string FullFilePath { get; set; }
        public string UTC { get; set; } = DateTime.UtcNow.ToString("R");

        public string ContentTypeToString()
        {
            string contentType;

            switch (MimeType)
            {
                case MIMEType.HTML:
                    contentType = "text/html";
                    break;
                case MIMEType.CSS:
                    contentType = "text/css";
                    break;
                case MIMEType.JPEG:
                    contentType = "image/jpeg";
                    break;
                case MIMEType.PNG:
                    contentType = "image/png";
                    break;
                case MIMEType.BMP:
                    contentType = "image/bmp";
                    break;
                case MIMEType.GIF:
                    contentType = "image/gif";
                    break;
                case MIMEType.TIFF:
                    contentType = "image/tiff";
                    break;
                case MIMEType.MP3:
                    contentType = "audio/mpeg3";
                    break;
                case MIMEType.AIFF:
                    contentType = "audio/aiff";
                    break;
                case MIMEType.WAV:
                    contentType = "audio/wav";
                    break;
                case MIMEType.MPEG:
                    contentType = "video/mpeg";
                    break;
                case MIMEType.AVI:
                    contentType = "video/avi";
                    break;
                case MIMEType.PLAIN:
                    contentType = "text/plain";
                    break;
                default:
                    contentType = "text/plain";
                    break;
            }

            return $"Content-Type: {contentType}\r\n";
        }

        public string ContentLengthToString(int length)
        {
            return $"Content-Length: {length}\r\n";
        }

        public string StatusCodeToString()
        {
            string status;

            switch (StatusCode)
            {
                case StatusCodeType.OK:
                    status = "HTTP/ 1.1 200 OK\r\n";
                    break;
                case StatusCodeType.BadRequest:
                    status = "HTTP/ 1.1 400 Bad Request\r\n";
                    break;
                case StatusCodeType.InternalServerError:
                    status = "HTTP/ 1.1 500 Internal Server Error\r\n";
                    break;
                default:
                    status = "HTTP/ 1.1 100 Continue\r\n";
                    break;
            }

            return status;
        }

        public string HeaderToString()
        {
            return StatusCodeToString() +
                $"Date: {UTC}\r\n" +
                $"Server: {Environment.MachineName}\r\n";
        }

        public string ErrorCodeHtml()
        {
            string e500 =
                "\r\n<html><head><title>Error</title></head><body><h1>Error 500 Internal Server Error</h1></body></html>";

            string e400 =
                "\r\n<html><head><title>Error</title></head><body><h1>Error 400 Bad Request</h1></body></html>";

            string e404 =
                "\r\n<html><head><title>Error</title></head><body><h1>Error 404 File Not Found</h1></body></html>";

            if (StatusCode == StatusCodeType.BadRequest)
            {
                return ContentTypeToString() + ContentLengthToString(e400.Length) + e400;
            }

            if (StatusCode == StatusCodeType.InternalServerError)
            {
                return ContentTypeToString() + ContentLengthToString(e500.Length) + e500;
            }

            if (StatusCode == StatusCodeType.FileNotFound)
            {
                return ContentTypeToString() + ContentLengthToString(e404.Length) + e404;
            }

            return string.Empty;
        }

        public void SetMimeType(string fileExtension)
        {
            MIMEType type;

            switch (fileExtension)
            {
                case ".html":
                    type = MIMEType.HTML;
                    break;
                case ".htm":
                    type = MIMEType.HTML;
                    break;
                case ".css":
                    type = MIMEType.CSS;
                    break;
                case ".jpeg":
                    type = MIMEType.JPEG;
                    break;
                case ".jpg":
                    type = MIMEType.JPEG;
                    break;
                case ".jpe":
                    type = MIMEType.JPEG;
                    break;
                case ".png":
                    type = MIMEType.PNG;
                    break;
                case ".bmp":
                    type = MIMEType.BMP;
                    break;
                case ".gif":
                    type = MIMEType.GIF;
                    break;
                case ".tiff":
                    type = MIMEType.TIFF;
                    break;
                case ".tif":
                    type = MIMEType.TIFF;
                    break;
                case ".aiff":
                    type = MIMEType.AIFF;
                    break;
                case ".txt":
                    type = MIMEType.PLAIN;
                    break;
                case ".avi":
                    type = MIMEType.AVI;
                    break;
                case ".mp3":
                    type = MIMEType.MP3;
                    break;
                case ".mpeg":
                    type = MIMEType.MPEG;
                    break;
                case ".wav":
                    type = MIMEType.WAV;
                    break;
                default:
                    type = MIMEType.PLAIN;
                    break;
            }

            MimeType = type;
        }
    }
}
