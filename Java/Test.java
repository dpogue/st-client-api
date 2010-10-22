import com.google.api.translate.Language;
import com.google.api.translate.Translate;
import com.socialtext.Resting;
import com.socialtext.Signal;
import com.socialtext.Person;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Test {

    public static void main(String[] args) {
        String url = "https://developers.socialtext.net";
        String username = args[0];
        String password = args[1];

        Resting r = new Resting(url, username, password);
        
        r.pollSignals();

        /*ArrayList<Signal> sigs = r.getSignals();

        for (int i = 0; i < sigs.size(); i++) {
            System.out.println(sigs.get(i));
        }

        //Get all people the user can see
        ArrayList<Person> peops = r.getPeople();

        for (int i = 0; i < peops.size(); i++) {
            System.out.println(peops.get(i));
        }

        String translatedText;
        try {
            translatedText = Translate.execute("Sending a signal from Java... in french", Language.ENGLISH, Language.FRENCH);
            Signal dox = new Signal();
            dox.setBody(translatedText);
            r.postSignal(dox);
        } catch (Exception ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
}
