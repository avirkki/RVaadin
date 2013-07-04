/*
* Copyright 2013 VTT Technical Research Centre of Finland
*
* Licensed under the Apache License, Version 2.0 (the "License"); you may not
* use this file except in compliance with the License. You may obtain a copy of
* the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations under
* the License.
*/

package fi.vtt.RVaadin;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.*;

import com.vaadin.server.StreamResource.StreamSource;

/**
 * 
 * @author arho
 * 
 */
public class RDownloadSource implements StreamSource {

	private static final long serialVersionUID = 1L;

	private RContainer R;
	private RConnection rc;
	private String filename = null;

	public RDownloadSource(String filename, RContainer R) {
		this.R = R;
		this.filename = filename;
	}

	@Override
	public InputStream getStream() {
		try {
			rc = R.getRConnection();

			/* We use R to turn the file into a raw binary stream. First we need
			 * to ask how large the file is, and then read it with 'readBin' */
			long fileSizeBytes = rc.parseAndEval(
					"file.info('" + filename + "')$size").asInteger();
			REXP xp = rc.parseAndEval("r <- readBin('" + filename + "','raw',"
					+ fileSizeBytes + "); r");

			return new ByteArrayInputStream(xp.asBytes());

		} catch (Exception e) {
			e.printStackTrace();
			return null;

		} finally {
			/* Finally release the connection-specific semaphore in all possible
			 * cases */
			R.releaseRConnection();
		}
	}
}
