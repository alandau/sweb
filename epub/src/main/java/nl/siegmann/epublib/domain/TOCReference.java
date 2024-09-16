package nl.siegmann.epublib.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * An item in the Table of Contents.
 * 
 * @see nl.siegmann.epublib.domain.TableOfContents
 * 
 * @author paul
 *
 */
public class TOCReference extends TitledResourceReference implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5787958246077042456L;
	private List<TOCReference> children;
	private static final Comparator<TOCReference> COMPARATOR_BY_TITLE_IGNORE_CASE = new Comparator<TOCReference>() {

		@Override
		public int compare(TOCReference tocReference1, TOCReference tocReference2) {
			return String.CASE_INSENSITIVE_ORDER.compare(tocReference1.getTitle(), tocReference2.getTitle());
		}
	};
	
	public TOCReference() {
		this(null, null, null);
	}
	
	public TOCReference(String name, Resource resource) {
		this(name, resource, null);
	}
	
	public TOCReference(String name, Resource resource, String fragmentId) {
		this(name, resource, fragmentId, new ArrayList<TOCReference>());
	}
	
	public TOCReference(String title, Resource resource, String fragmentId, List<TOCReference> children) {
		super(resource, title, fragmentId);
		this.children = children;
	}

	public static Comparator<TOCReference> getComparatorByTitleIgnoreCase() {
		return COMPARATOR_BY_TITLE_IGNORE_CASE;
	}
	
	public List<TOCReference> getChildren() {
		return children;
	}

	public TOCReference addChildSection(TOCReference childSection) {
		this.children.add(childSection);
		return childSection;
	}
	
	public void setChildren(List<TOCReference> children) {
		this.children = children;
	}
}
