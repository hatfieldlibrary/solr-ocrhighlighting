package de.digitalcollections.solrocr.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;

/** A structured representation of a highlighted OCR snippet. */
public class OcrSnippet {
  private final String text;
  private final List<OcrPage> pages;
  private final List<OcrBox> snippetRegions;
  private final List<OcrBox[]> highlightRegions;
  private float score;

  /**
   * Create a new snippet on the given region on the page along with its plaintext.
   * @param text plaintext version of the highlighted page text with highlighting tags
   * @param pages Pages this snippet appears on
   * @param snippetRegions regions the snippet is located in
   */
  public OcrSnippet(String text, List<OcrPage> pages, List<OcrBox> snippetRegions) {
    this.text = text;
    this.pages = pages;
    this.snippetRegions = snippetRegions;
    this.highlightRegions = new ArrayList<>();
  }

  /** Add a new highlighted region in the snippet.
   *
   * <strong>Note that the region should be relative to the snippet region!</strong>
   *
   * @param region Location of the highlighted region <strong>relative to the snippet region</strong>.
   */
  public void addHighlightRegion(List<OcrBox> region) {
    this.highlightRegions.add(region.toArray(new OcrBox[0]));
  }

  /** Get the plaintext version of the highlighted page text with highlighting tags */
  public String getText() {
    return text;
  }

  /** Get the region of the page that the snippes is located in */
  public List<OcrBox> getSnippetRegions() {
    return snippetRegions;
  }

  /**
   * Get the highlighted regions of the snippet region.
   *
   * <strong>The highlighted regions are relative to the snippet region, not to the page.</strong>
   */
  public List<OcrBox[]> getHighlightRegions() {
    return highlightRegions;
  }

  /** Get the score of the passage, compared to all other passages in the document */
  public float getScore() {
    return score;
  }

  /** Set the score of the passage, compared to all other passages in the document */
  public void setScore(float score) {
    this.score = score;
  }

  /** Convert the snippet to a {@link NamedList} that is used by Solr to populate the response. */
  @SuppressWarnings("rawtypes")
  public NamedList toNamedList() {
    SimpleOrderedMap m = new SimpleOrderedMap();
    m.add("text", this.getText());
    m.add("score", this.getScore());
    NamedList[] pageEntries = this.pages.stream()
        .map(OcrPage::toNamedList).toArray(NamedList[]::new);
    m.add("pages", pageEntries);
    NamedList[] snips = this.snippetRegions.stream()
        .map(OcrBox::toNamedList).toArray(NamedList[]::new);
    m.add("regions", snips);
    if (this.getHighlightRegions() != null) {
      List<NamedList[]> highlights = new ArrayList<>();
      for (OcrBox[] region : this.getHighlightRegions()) {
        NamedList[] regionBoxes = Arrays.stream(region)
            .map(OcrBox::toNamedList).toArray(NamedList[]::new);
        highlights.add(regionBoxes);
      }
      m.add("highlights", highlights);
    }
    return m;
  }
}
