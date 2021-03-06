﻿using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace Socialtext.Client
{
    public partial class SignalPanel : TableLayoutPanel
    {
        /// <summary> 
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        private Signal fSignal;
        private List<Signal> fReplies;

        public Int32 SignalID
        {
            get { return fSignal.SignalID; }
        }

        public SignalPanel(Signal sig)
        {
            fSignal = sig;
            fReplies = new List<Signal>();

            this.BackColor = Color.White;
            this.AutoSize = true;
            this.AutoSizeMode = System.Windows.Forms.AutoSizeMode.GrowOnly;

            this.ColumnCount = 3;
            this.ColumnStyles.Clear();
            this.ColumnStyles.Add(new ColumnStyle(SizeType.Absolute, 64));
            this.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 50));
            this.ColumnStyles.Add(new ColumnStyle(SizeType.Percent, 50));

            this.RowCount = 2;
            this.RowStyles.Clear();
            this.RowStyles.Add(new RowStyle(SizeType.AutoSize));
            this.RowStyles.Add(new RowStyle(SizeType.AutoSize));

            PictureBox photo = new PictureBox();
            photo.Image = sig.Person.GetPhoto(SignalsForm.Client);
            photo.Height = 64;
            photo.Width = 64;

            Label username = new Label();
            username.Text = fSignal.BestFullName;
            username.Font = new System.Drawing.Font(username.Font, FontStyle.Bold);

            Label body = new Label();
            body.Text = fSignal.Body;
            //body.AutoSize = true;
            body.Anchor = AnchorStyles.Top | AnchorStyles.Left | AnchorStyles.Bottom | AnchorStyles.Right;
            body.Dock = DockStyle.Fill;
            

            this.Controls.Add(photo, 0, 0);
            this.SetRowSpan(photo, 2);

            this.Controls.Add(username, 1, 0);

            this.Controls.Add(body, 1, 1);
            this.SetColumnSpan(body, 2);
        }

        /// <summary> 
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        public void AddReply(Signal s)
        {
            this.RowCount++;
            this.fReplies.Add(s);

            SignalPanel reply = new SignalPanel(s);

            this.Controls.Add(reply, 1, this.RowCount - 1);
            this.SetColumnSpan(reply, 2);
        }
    }
}
