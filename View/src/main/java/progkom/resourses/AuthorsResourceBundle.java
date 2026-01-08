package progkom.resourses;

import java.util.ListResourceBundle;

public class AuthorsResourceBundle extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                {"authors", new String[] {"Hubert Szczecinski"}}
        };
    }
}