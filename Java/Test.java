import com.socialtext.Resting;
import com.socialtext.Signal;
import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class Test
{
    public static void main(String[] args) throws Exception
    {
        String url = "https://developers.socialtext.net";
        String username = args[0];
        String password = args[1];
        Translate.setHttpReferrer("localhost");
        Resting r = new Resting(url, username, password);
        
        Signal[] sigs = r.getSignals();

        for (int i = 0; i < sigs.length; i++) {
            System.out.println(sigs[i]);
        }

        String translatedText = Translate.execute("Sending a signal from Java... in french"
                , Language.ENGLISH, Language.FRENCH);

        // Testing a post
        Signal dox = new Signal();
        System.out.println(translatedText);
        dox.setBody(translatedText);
        r.postSignal(dox);
    }
}
