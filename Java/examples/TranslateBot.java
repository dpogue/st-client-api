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

/**
 * An automatic translation service for Socialtext Signals.
 *
 * This runs as a daemon listening for replies to signals consisting of the
 * word "translate".
 * It will then fetch the parent signal, run it through Google's language
 * detection and translation API, and add a reply with the translated text.
 */
public class TranslateBot
{
    /**
     * Main application loop for the translation daemon.
     *
     * This will prompt for a Socialtext URL, a username, and a password,
     * then connect and listen for signals using the push API.
     */
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

                // Create a Socialtext Resting client with the specified data
                Resting m_client = new Resting(url, login, new String(passwd));
                java.util.Arrays.fill(passwd, ' ');
                
                System.out.println();

                GoogleAPI.setHttpReferrer(url);

                while (true)
                {
                    // Open a push request to fetch signals as they are posted
                    ArrayList<Signal> incoming = m_client.pollSignals();

                    if (incoming != null)
                    {
                        // Loop over the received signals
                        for (Signal s : incoming)
                        {
                            // We only care about it if it is a reply with the content "translate"
                            if (s.getReplyID() != -1 && s.getBody().contains("translate"))
                            {
                                // Fetch the parent signal
                                Signal parent = m_client.getSignal(s.getReplyID());

                                try
                                {
                                    // Get the text and detect the language
                                    String text = parent.getBody();
                                    Language ln = Detect.execute(text).getLanguage();

                                    // Translate the text
                                    String translated = Translate.execute(text, ln, Language.ENGLISH);

                                    // Post a reply with the translated text
                                    Signal reply = new Signal();
                                    reply.setBody("Translation: " + translated);
                                    reply.setReply(parent);
                                    m_client.postSignal(reply);
                                }
                                catch (Exception e)
                                {
                                    // Error handling
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
