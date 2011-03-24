package org.micropsi.media;

import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;

public interface IVideoSourceProvider {

	public DataSource getVideoSource();
	
	public VideoFormat getFormat();
}
