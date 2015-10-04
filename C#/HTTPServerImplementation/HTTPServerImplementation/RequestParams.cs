using System;

namespace HTTPServerImplementation
{
    internal class RequestParams
    {
        public bool IsValid { get; set; } = false;
        public bool HTTP11 { get; set; } = false;

        public MIMEType MimeType { get; set; } = MIMEType.PLAIN;

        public string ServerDirectory { get; set; }
        public string FullFilePath { get; set; }
        public string UTC { get; set; } = DateTime.UtcNow.ToString("R");

        public string ContentType()
        {
            string contentType;

            switch (MimeType)
            {
                case MIMEType.HTML:
                    contentType = "text/html";
                    break;
                case MIMEType.JPEG:
                    contentType = "image/jpeg";
                    break;
                case MIMEType.MP3:
                    contentType = "audio/mpeg3";
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

        public string ContentLength(int length)
        {
            return $"Content-Length: {length}\r\n";
        }

        private string StatusCode(StatusCodeType code)
        {
            string status;

            switch (code)
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

        public string Header(StatusCodeType code)
        {
            return StatusCode(code) +
                $"Date: {UTC}\r\n" +
                $"Server: {Environment.MachineName}/2.1\r\n";
        }
    }
}
