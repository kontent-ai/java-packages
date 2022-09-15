package kentico.kontent.delivery.nestedmodels;

import kentico.kontent.delivery.ContentItemMapping;
import kentico.kontent.delivery.ElementMapping;
import kentico.kontent.delivery.System;

import java.util.List;

@ContentItemMapping("page")
public class Page {
  @ElementMapping("title")
  String title;

  // Custom strongly type mapping to List
  @ContentItemMapping("sections")
  List<Section> sections;

  System system;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public List<Section> getSections() {
    return sections;
  }

  public void setSections(List<Section> sections) {
    this.sections = sections;
  }

  public System getSystem() {
    return system;
  }

  public void setSystem(System system) {
    this.system = system;
  }
}
