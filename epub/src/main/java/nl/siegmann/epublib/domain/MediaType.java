package nl.siegmann.epublib.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;

/**
 * MediaType is used to tell the type of content a resource is.
 * 
 * Examples of mediatypes are image/gif, text/css and application/xhtml+xml
 * 
 * All allowed mediaTypes are maintained bye the MediaTypeService.
 * 
 * @see nl.siegmann.epublib.service.MediatypeService
 * 
 * @author paul
 *
 */
public class MediaType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7256091153727506788L;
	private String name;
	private String defaultExtension;
	private Collection<String> extensions;
	
	public MediaType(String name, String defaultExtension) {
		this(name, defaultExtension, new String[] {defaultExtension});
	}

	public MediaType(String name, String defaultExtension,
			String[] extensions) {
		this(name, defaultExtension, Arrays.asList(extensions));
	}
	
	public int hashCode() {
		if (name == null) {
			return 0;
		}
		return name.hashCode();
	}
	public MediaType(String name, String defaultExtension,
			Collection<String> extensions) {
		super();
		this.name = name;
		this.defaultExtension = defaultExtension;
		this.extensions = extensions;
	}


	public String getName() {
		return name;
	}


	public String getDefaultExtension() {
		return defaultExtension;
	}


	public Collection<String> getExtensions() {
		return extensions;
	}
	
	public boolean equals(Object otherMediaType) {
		if(! (otherMediaType instanceof MediaType)) {
			return false;
		}
		return name.equals(((MediaType) otherMediaType).getName());
	}

	public String toString() {
		return name;
	}
}
