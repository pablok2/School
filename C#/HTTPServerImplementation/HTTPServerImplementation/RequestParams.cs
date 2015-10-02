namespace HTTPServerImplementation
{
    class RequestParams
    {
        public bool IsValid { get; set; }
        public string ServerDirectory { get; set; }
        public bool HTTP11 { get; set; }
        public MIMEType MimeType { get; set; }
    }
}
