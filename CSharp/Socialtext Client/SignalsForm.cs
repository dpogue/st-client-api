using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Socialtext;

namespace Socialtext.Client
{
    public partial class SignalsForm : Form
    {

        public static RestClient Client;

        public SignalsForm()
        {
            InitializeComponent();
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
                /* TODO: Move this to a thread, and do it somewhere else */
                List<Signal> sigs = Signal.Get(Client);

                foreach (Signal s in sigs)
                {
                    SignalPanel sp = new SignalPanel(s);
                    sp.MinimumSize = new Size(this.flowLayoutPanel1.Width - 30, 64);
                    sp.MaximumSize = new Size(this.flowLayoutPanel1.Width - 30, 300);
                    this.flowLayoutPanel1.Controls.Add(sp);
                }
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
