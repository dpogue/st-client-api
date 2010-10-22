import com.google.api.GoogleAPI;
import com.google.api.detect.Detect;
import com.google.api.detect.DetectResult;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;
import com.socialtext.Resting;
import com.socialtext.Signal;
import java.io.Console;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TranslateBot
{
    public static void main(String[] args)
    {
        Console con;

        if ((con = System.console()) != null)
        {
            try
            {
                String url = con.readLine("%s: ", "Socialtext URL");
                String login = con.readLine("%s: ", "Username");
                char[] passwd = con.readPassword("%s: ", "Password");

                Resting m_client = new Resting(url, login, new String(passwd));
                java.util.Arrays.fill(passwd, ' ');
                
                System.out.println();

                GoogleAPI.setHttpReferrer(url);

                while (true)
                {
                    ArrayList<Signal> incoming = m_client.pollSignals();

                    if (incoming != null)
                    {
                        for (Signal s : incoming)
                        {
                            if (s.getReplyID() != -1 && s.getBody().contains("translate"))
                            {
                                Signal parent = m_client.getSignal(s.getReplyID());

                                try
                                {
                                    String text = parent.getBody();
                                    Language ln = Detect.execute(text).getLanguage();

                                    String translated = Translate.execute(text, ln, Language.ENGLISH);

                                    Signal reply = new Signal();
                                    reply.setBody("Translation: " + translated);
                                    reply.setReply(parent);
                                    m_client.postSignal(reply);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            catch (IllegalArgumentException iae)
            {
                iae.printStackTrace();
                return;
            }
        }
    }
}
