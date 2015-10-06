using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.ServiceProcess;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace HTTPServerService
{
    public partial class HTTPService : ServiceBase
    {
        private Socket _listeningSocket;
        private Thread _hostThread;
        private string _rootDirectory = "C:\\";
        private string _portNumber = "80";
        private int _port;

        public HTTPService(string[] args)
        {
            InitializeComponent();
        }

        protected override void OnStart(string[] args)
        {
            // Args
            if (args.Count() > 0)
            {
                _rootDirectory = args[0];
            }

            if (args.Count() > 1)
            {
                _portNumber = args[1];
            }

            if (!int.TryParse(_portNumber, out _port))
            {
                // Default port 80
                _port = 80;
            }

            if (Directory.Exists(_rootDirectory))
            {
                
                _hostThread = new Thread(RunHTTPService);
                _hostThread.Start();
            }
            else
            {
                OnStop();
            }
        }

        protected override void OnStop()
        {
            _listeningSocket?.Close();
        }

        private void RunHTTPService()
        {
            // Run the service
            _listeningSocket = new Socket(AddressFamily.InterNetwork, SocketType.Stream, ProtocolType.Tcp);
            _listeningSocket.Bind(new IPEndPoint(IPAddress.Parse("127.0.0.1"), _port)); // Always localhost
            _listeningSocket.Listen(_port);

            while (true)
            {
                WebRequest newWebRequest = new WebRequest(_listeningSocket.Accept())
                {
                    RootDirectory = _rootDirectory
                };

                Thread thread = new Thread(() => newWebRequest.StartProcessing());
                thread.Start();
            }
        }
    }
}
