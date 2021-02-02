package kentico.kontent.delivery.nestedmodels;

import kentico.kontent.delivery.ContentItemMapping;
import kentico.kontent.delivery.ElementMapping;
import kentico.kontent.delivery.System;

import java.util.List;

@ContentItemMapping("section")
public class Section {

  // Custom strongly type mapping to List
  @ContentItemMapping("section_parts")
  List<SectionPart> sectionParts;

  @ElementMapping("headline")
  String headline;

  System system;

  public List<SectionPart> getSectionParts() {
    return sectionParts;
  }

  public void setSectionParts(List<SectionPart> sectionParts) {
    this.sectionParts = sectionParts;
  }

  public String getHeadline() {
    return headline;
  }

  public void setHeadline(String headline) {
    this.headline = headline;
  }

  public System getSystem() {
    return system;
  }

  public void setSystem(System system) {
    this.system = system;
  }
}
