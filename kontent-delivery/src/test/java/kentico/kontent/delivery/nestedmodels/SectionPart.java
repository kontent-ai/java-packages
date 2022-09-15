package kentico.kontent.delivery.nestedmodels;

import kentico.kontent.delivery.ContentItemMapping;
import kentico.kontent.delivery.ElementMapping;
import kentico.kontent.delivery.System;

@ContentItemMapping("section_part")
public class SectionPart {

  // Custom strongly type mapping to List
  @ElementMapping("content")
  String content;

  @ElementMapping("title")
  String title;

  System system;

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public System getSystem() {
    return system;
  }

  public void setSystem(System system) {
    this.system = system;
  }
}
