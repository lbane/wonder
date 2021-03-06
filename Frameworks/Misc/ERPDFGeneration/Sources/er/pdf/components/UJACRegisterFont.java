package er.pdf.components;

import com.webobjects.appserver.WOAssociation;
import com.webobjects.appserver.WOElement;
import com.webobjects.foundation.NSDictionary;

public class UJACRegisterFont extends UJACResource {

  public UJACRegisterFont(String name, NSDictionary<String, WOAssociation> associations, WOElement template) {
    super("register-font", associations, template);
  }

}
