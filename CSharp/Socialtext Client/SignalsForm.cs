using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Threading;
using System.Windows.Forms;
using Socialtext;

namespace Socialtext.Client
{
    public partial class SignalsForm : Form
    {

        public static RestClient Client;

        private Dictionary<Int32, Signal> fSignalMap;
        private PushClient fPush;
        private readonly Object fSync = new Object();

        private delegate void RESTCall();
        private delegate void UICallback(Signal s);

        public SignalsForm()
        {
            fSignalMap = new Dictionary<Int32, Signal>();
            InitializeComponent();
        }

        ~SignalsForm()
        {
            if (fPush != null)
            {
                fPush.Shutdown();
            }
        }

        private void LoadSignals()
        {
            Thread t1 = new Thread(LoadInitialSignals);
            t1.Start();
        }

        private void LoadInitialSignals()
        {
            List<Signal> signals = Signal.Get(Client);

            foreach (Signal s in signals)
            {
                fSignalMap.Add(s.SignalID, s);
            }

            foreach (Signal s in signals)
            {
                GotASignal(s);
            }

            StartPushClient();
        }

        private void StartPushClient()
        {
            fPush = Client.GetPushClient();
            fPush.OnSignalPosted += new PushClient.SignalDelegate(GotASignal);
            fPush.Run();
        }

        private void GotASignal(Signal s)
        {
            if (!fSignalMap.ContainsKey(s.SignalID))
            {
                fSignalMap.Add(s.SignalID, s);
            }

            if (s.InReplyTo != null)
            {
                try
                {
                    s.InReplyTo = fSignalMap[s.InReplyTo.SignalID];
                    this.BeginInvoke(new UICallback(AddSignalReply), new object[] { s });
                }
                catch (KeyNotFoundException)
                {
                    this.BeginInvoke(new UICallback(AddSignalPanel), new object[] { s });
                }
            }
            else
            {
                this.BeginInvoke(new UICallback(AddSignalPanel), new object[] { s });
            }
        }

        private void AddSignalPanel(Signal s)
        {
            SignalPanel sp = new SignalPanel(s);
            sp.MinimumSize = new Size(this.flowLayoutPanel1.Width - 30, 64);
            sp.MaximumSize = new Size(this.flowLayoutPanel1.Width - 30, 300);
            this.flowLayoutPanel1.Controls.Add(sp);
        }

        private void AddSignalReply(Signal s)
        {
            Int32 parentID = s.InReplyTo.SignalID;
            SignalPanel parent = null;

            foreach (Control c in this.flowLayoutPanel1.Controls)
            {
                if (!(c is SignalPanel))
                    continue;

                SignalPanel sp = c as SignalPanel;
                if (sp.SignalID == parentID)
                {
                    parent = sp;
                }
            }

            if (parent == null)
            {
                parent = new SignalPanel(s.InReplyTo);
                parent.MinimumSize = new Size(this.flowLayoutPanel1.Width - 30, 64);
                parent.MaximumSize = new Size(this.flowLayoutPanel1.Width - 30, 300);
                this.flowLayoutPanel1.Controls.Add(parent);
            }

            parent.AddReply(s);
        }

        private void SignalsForm_Load(object sender, EventArgs e)
        {
            LoginForm lf = new LoginForm();
            DialogResult dr = lf.ShowDialog();
            while (dr == System.Windows.Forms.DialogResult.Retry)
            {
                MessageBox.Show("Invalid host, username, or password");
                dr = lf.ShowDialog();
            }

            if (dr == System.Windows.Forms.DialogResult.OK)
            {
                this.LoadSignals();
            }
            else
            {
                dr = MessageBox.Show("Do you wish to quit Socialtext Signals?", "Socialtext", MessageBoxButtons.YesNo, MessageBoxIcon.None, MessageBoxDefaultButton.Button2);
                if (dr == System.Windows.Forms.DialogResult.Yes)
                {
                    this.Close();
                }
                else
                {
                    SignalsForm_Load(sender, e);
                }
            }
        }
    }
}
