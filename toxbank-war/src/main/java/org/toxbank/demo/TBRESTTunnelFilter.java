package org.toxbank.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CharacterSet;
import org.restlet.data.ClientInfo;
import org.restlet.data.Encoding;
import org.restlet.data.Form;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Metadata;
import org.restlet.data.Method;
import org.restlet.data.Parameter;
import org.restlet.data.Preference;
import org.restlet.data.Reference;
import org.restlet.engine.Engine;
import org.restlet.engine.http.HttpConstants;
import org.restlet.engine.http.PreferenceUtils;
import org.restlet.routing.Filter;
import org.restlet.service.MetadataService;
import org.restlet.service.TunnelService;
import org.restlet.util.Series;

public class TBRESTTunnelFilter extends Filter {

	public TBRESTTunnelFilter(Context context) {
		super(context);

	}
	   /**
     * Used to describe the replacement value for an old client preference and
     * for a a series of specific agent (i.e. web client) attributes.
     * 
     * @author Thierry Boileau
     * 
     */
    private static class AcceptReplacer {
        /** New accept header value. */
        private volatile String acceptNew;

        /** Old accept header value. */
        private volatile String acceptOld;

        /** Agent attributes that must be checked. */
        private volatile Map<String, String> agentAttributes;

        public String getAcceptNew() {
            return acceptNew;
        }

        public String getAcceptOld() {
            return acceptOld;
        }

        public Map<String, String> getAgentAttributes() {
            if (agentAttributes == null) {
                agentAttributes = new HashMap<String, String>();
            }

            return agentAttributes;
        }

        public void setAcceptNew(String acceptNew) {
            this.acceptNew = acceptNew;
        }

        public void setAcceptOld(String acceptOld) {
            this.acceptOld = acceptOld;
        }
    }

    /** Used to replace accept header values. */
    private volatile List<AcceptReplacer> acceptReplacers;


    @Override
    public int beforeHandle(Request request, Response response) {
        if (getTunnelService().isUserAgentTunnel()) {
            processUserAgent(request);
        }

        if (getTunnelService().isExtensionsTunnel()) {
            processExtensions(request);
        }

        if (getTunnelService().isQueryTunnel()) {
            processQuery(request);
        }

        if (getTunnelService().isHeadersTunnel()) {
            processHeaders(request);
        }

        return CONTINUE;
    }

    /**
     * Returns the list of new accept header values. Each of them describe also
     * a set of conditions required to set the new value.
     * 
     * @return The list of new accept header values.
     */
    private List<AcceptReplacer> getAcceptReplacers() {
        // Lazy initialization with double-check.
        List<AcceptReplacer> a = this.acceptReplacers;
        if (a == null) {
            synchronized (this) {
                a = this.acceptReplacers;
                if (a == null) {
                    this.acceptReplacers = a = new ArrayList<AcceptReplacer>();

                    // Load the accept.properties file.
                    final URL userAgentPropertiesUrl = Engine.getClassLoader()
                            .getResource(
                                    "org/restlet/service/accept.properties");
                    if (userAgentPropertiesUrl != null) {
                        BufferedReader reader;
                        try {
                            reader = new BufferedReader(new InputStreamReader(
                                    userAgentPropertiesUrl.openStream(),
                                    CharacterSet.UTF_8.getName()));

                            AcceptReplacer acceptReplacer = new AcceptReplacer();

                            // Read the entire file, excluding comment lines
                            // starting with "#" character.
                            String line = reader.readLine();
                            for (; line != null; line = reader.readLine()) {
                                if (!line.startsWith("#")) {
                                    final String[] keyValue = line.split(":");
                                    if (keyValue.length == 2) {
                                        final String key = keyValue[0].trim();
                                        final String value = keyValue[1].trim();
                                        if ("acceptOld".equalsIgnoreCase(key)) {
                                            acceptReplacer.setAcceptOld((""
                                                    .equals(value)) ? null
                                                    : value);
                                        } else if ("acceptNew"
                                                .equalsIgnoreCase(key)) {
                                            acceptReplacer.setAcceptNew(value);
                                            this.acceptReplacers
                                                    .add(acceptReplacer);

                                            acceptReplacer = new AcceptReplacer();
                                        } else {
                                            acceptReplacer.getAgentAttributes()
                                                    .put(key, value);
                                        }
                                    }
                                }
                            }

                            reader.close();
                        } catch (IOException e) {
                            getContext().getLogger().warning(
                                    "Cannot read '"
                                            + userAgentPropertiesUrl.toString()
                                            + "' due to: " + e.getMessage());
                        }
                    }
                }
            }
        }
        return a;
    }

    /**
     * Returns the metadata associated to the given extension using the
     * {@link MetadataService}.
     * 
     * @param extension
     *            The extension to lookup.
     * @return The matched metadata.
     */
    private Metadata getMetadata(String extension) {
        return getMetadataService().getMetadata(extension);
    }

    /**
     * Returns the metadata service of the parent application.
     * 
     * @return The metadata service of the parent application.
     */
    public MetadataService getMetadataService() {
        return getApplication().getMetadataService();
    }

    /**
     * Returns the tunnel service of the parent application.
     * 
     * @return The tunnel service of the parent application.
     */
    public TunnelService getTunnelService() {
        return getApplication().getTunnelService();
    }

    /**
     * Updates the client preferences based on file-like extensions. The matched
     * extensions are removed from the last segment.
     * 
     * See also section 3.6.1 of JAX-RS specification (<a
     * href="https://jsr311.dev.java.net">https://jsr311.dev.java.net</a>)
     * 
     * @param request
     *            The request to update.
     * @return True if the query has been updated, false otherwise.
     */
    private boolean processExtensions(Request request) {
        final TunnelService tunnelService = getTunnelService();
        boolean extensionsModified = false;

        // Tunnel the client preferences only for GET or HEAD requests
        final Method method = request.getMethod();
        if (tunnelService.isPreferencesTunnel()
                && (method.equals(Method.GET) || method.equals(Method.HEAD))) {
            final Reference resourceRef = request.getResourceRef();

            if (resourceRef.hasExtensions()) {
                final ClientInfo clientInfo = request.getClientInfo();
                boolean encodingFound = false;
                boolean characterSetFound = false;
                boolean mediaTypeFound = false;
                boolean languageFound = false;
                String extensions = resourceRef.getExtensions();

                // Discover extensions from right to left and stop at the first
                // unknown extension. Only one extension per type of metadata is
                // also allowed: i.e. one language, one media type, one
                // encoding, one character set.
                while (true) {
                    final int lastIndexOfPoint = extensions.lastIndexOf('.');
                    final String extension = extensions
                            .substring(lastIndexOfPoint + 1);
                    final Metadata metadata = getMetadata(extension);

                    if (!mediaTypeFound && (metadata instanceof MediaType)) {
                        updateMetadata(clientInfo, metadata);
                        mediaTypeFound = true;
                    } else if (!languageFound && (metadata instanceof Language)) {
                        updateMetadata(clientInfo, metadata);
                        languageFound = true;
                    } else if (!characterSetFound
                            && (metadata instanceof CharacterSet)) {
                        updateMetadata(clientInfo, metadata);
                        characterSetFound = true;
                    } else if (!encodingFound && (metadata instanceof Encoding)) {
                        updateMetadata(clientInfo, metadata);
                        encodingFound = true;
                    } else {
                        // extension do not match -> break loop
                        break;
                    }
                    if (lastIndexOfPoint > 0) {
                        extensions = extensions.substring(0, lastIndexOfPoint);
                    } else {
                        // no more extensions -> break loop
                        extensions = "";
                        break;
                    }
                }

                // Update the extensions if necessary
                if (encodingFound || characterSetFound || mediaTypeFound
                        || languageFound) {
                    resourceRef.setExtensions(extensions);
                    extensionsModified = true;
                }
            }
        }

        return extensionsModified;
    }

    /**
     * Updates the request method based on specific header.
     * 
     * @param request
     *            The request to update.
     */
    @SuppressWarnings("unchecked")
    private void processHeaders(Request request) {
        final TunnelService tunnelService = getTunnelService();

        if (tunnelService.isMethodTunnel()) {
            // get the headers
            final Series<Parameter> extraHeaders = (Series<Parameter>) request
                    .getAttributes().get(HttpConstants.ATTRIBUTE_HEADERS);

            if (extraHeaders != null) {
                // look for the new value of the method
                final String newMethodValue = extraHeaders.getFirstValue(
                        getTunnelService().getMethodHeader(), true);

                if (newMethodValue != null
                        && newMethodValue.trim().length() > 0) {
                    // set the current method to the new method
                    request.setMethod(Method.valueOf(newMethodValue));
                }
            }
        }
    }

    /**
     * Updates the request method and client preferences based on query
     * parameters. The matched parameters are removed from the query.
     * 
     * @param request
     *            The request to update.
     * @return True if the query has been updated, false otherwise.
     */
    private boolean processQuery(Request request) {
        TunnelService tunnelService = getTunnelService();
        boolean queryModified = false;
        Reference resourceRef = request.getResourceRef();

        if (resourceRef.hasQuery()) {
            Form query = resourceRef.getQueryAsForm();

            // Tunnel the request method
            Method method = request.getMethod();
            if (tunnelService.isMethodTunnel()) {
                String methodName = query.getFirstValue(tunnelService
                        .getMethodParameter());

                Method tunnelledMethod = Method.valueOf(methodName);
                // The OPTIONS method can be tunneled via GET requests.
                if (tunnelledMethod != null
                        && (Method.POST.equals(method) || Method.OPTIONS
                                .equals(tunnelledMethod))) {
                    request.setMethod(tunnelledMethod);
                    query.removeFirst(tunnelService.getMethodParameter());
                    queryModified = true;
                }
            }

            // Tunnel the client preferences
            if (tunnelService.isPreferencesTunnel()) {
                // Get the parameter names to look for
                String charSetParameter = tunnelService
                        .getCharacterSetParameter();
                String encodingParameter = tunnelService.getEncodingParameter();
                String languageParameter = tunnelService.getLanguageParameter();
                String mediaTypeParameter = tunnelService
                        .getMediaTypeParameter();

                // Get the preferences from the query
                String acceptedCharSet = query.getFirstValue(charSetParameter);
                String acceptedEncoding = query
                        .getFirstValue(encodingParameter);
                String acceptedLanguage = query
                        .getFirstValue(languageParameter);
                String acceptedMediaType = query
                        .getFirstValue(mediaTypeParameter);

                // Updates the client preferences
                ClientInfo clientInfo = request.getClientInfo();
                Metadata metadata = getMetadata(acceptedCharSet);

                if ((metadata == null) && (acceptedCharSet != null)) {
                    metadata = CharacterSet.valueOf(acceptedCharSet);
                }

                if (metadata instanceof CharacterSet) {
                    updateMetadata(clientInfo, metadata);
                    query.removeFirst(charSetParameter);
                    queryModified = true;
                }

                metadata = getMetadata(acceptedEncoding);

                if ((metadata == null) && (acceptedEncoding != null)) {
                    metadata = Encoding.valueOf(acceptedEncoding);
                }

                if (metadata instanceof Encoding) {
                    updateMetadata(clientInfo, metadata);
                    query.removeFirst(encodingParameter);
                    queryModified = true;
                }

                metadata = getMetadata(acceptedLanguage);

                if ((metadata == null) && (acceptedLanguage != null)) {
                    metadata = Language.valueOf(acceptedLanguage);
                }

                if (metadata instanceof Language) {
                    updateMetadata(clientInfo, metadata);
                    query.removeFirst(languageParameter);
                    queryModified = true;
                }

                metadata = getMetadata(acceptedMediaType);

                if ((metadata == null) && (acceptedMediaType != null)) {
                    metadata = MediaType.valueOf(acceptedMediaType);
                }

                if (metadata instanceof MediaType) {
                    updateMetadata(clientInfo, metadata);
                    query.removeFirst(mediaTypeParameter);
                    queryModified = true;
                }
            }

            // Update the query if it has been modified
            if (queryModified) {
                request.getResourceRef().setQuery(query.getQueryString(null));
            }
        }

        return queryModified;
    }

    /**
     * Updates the client preferences according to the user agent properties
     * (name, version, etc.) taken from the "agent.properties" file located in
     * the classpath. See {@link ClientInfo#getAgentAttributes()} for more
     * details.<br>
     * The list of new media type preferences is loaded from a property file
     * called "accept.properties" located in the classpath in the sub directory
     * "org/restlet/service". This property file is composed of blocks of
     * properties. One "block" of properties starts either with the beginning of
     * the properties file or with the end of the previous block. One block ends
     * with the "acceptNew" property which contains the value of the new accept
     * header. Here is a sample block.
     * 
     * <pre>
     * agentName: firefox
     * acceptOld: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,\*\/\*;q=0.5
     * acceptNew: application/xhtml+xml,text/html,text/xml;q=0.9,application/xml;q=0.9,text/plain;q=0.8,image/png,\*\/\*;q=0.5
     * </pre>
     * 
     * Each declared property is a condition that must be filled in order to
     * update the client preferences. For example "agentName: firefox" expresses
     * the fact this block concerns only "firefox" clients.
     * 
     * The "acceptOld" property allows to check the value of the current
     * "Accept" header. If the latest equals to the value of the "acceptOld"
     * property then the preferences will be updated. This is useful for Ajax
     * clients which looks like their browser (same agentName, agentVersion,
     * etc.) but can provide their own "Accept" header.
     * 
     * @param request
     *            the request to update.
     */
    private void processUserAgent(Request request) {
        final Map<String, String> agentAttributes = request.getClientInfo()
                .getAgentAttributes();
        if (agentAttributes != null) {
            if (getAcceptReplacers() != null) {
                // Get the old Accept header value
                final Form headers = (Form) request.getAttributes().get(
                        HttpConstants.ATTRIBUTE_HEADERS);

                final String acceptOld = (headers != null) ? headers
                        .getFirstValue(HttpConstants.HEADER_ACCEPT, true)
                        : null;

                // Check each replacer
                for (AcceptReplacer acceptReplacer : this.acceptReplacers) {
                    // Check the conditions
                    boolean checked = true;
                    for (String key : acceptReplacer.getAgentAttributes()
                            .keySet()) {
                        final String attribute = agentAttributes.get(key);
                        checked = checked
                                && (attribute != null && attribute
                                        .equals(acceptReplacer
                                                .getAgentAttributes().get(key)));

                    }
                    if (checked) {
                    	
                    	if (acceptReplacer.getAcceptOld()==null) 
                    		checked = true;
                    	else if (acceptOld == null) {
                            checked = acceptReplacer.getAcceptOld() == null;
                        } else {
                            checked = acceptOld.equals(acceptReplacer
                                    .getAcceptOld());
                        }
                        if (checked) {
                            final ClientInfo clientInfo = new ClientInfo();
                            PreferenceUtils.parseMediaTypes(acceptReplacer
                                    .getAcceptNew(), clientInfo);
                            request.getClientInfo().setAcceptedMediaTypes(
                                    clientInfo.getAcceptedMediaTypes());
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates the client info with the given metadata. It clears existing
     * preferences for the same type of metadata if necessary.
     * 
     * @param clientInfo
     *            The client info to update.
     * @param metadata
     *            The metadata to use.
     */
    private void updateMetadata(ClientInfo clientInfo, Metadata metadata) {
        if (metadata != null) {
            if (metadata instanceof CharacterSet) {
                clientInfo.getAcceptedCharacterSets().clear();
                clientInfo.getAcceptedCharacterSets().add(
                        new Preference<CharacterSet>((CharacterSet) metadata));
            } else if (metadata instanceof Encoding) {
                clientInfo.getAcceptedEncodings().clear();
                clientInfo.getAcceptedEncodings().add(
                        new Preference<Encoding>((Encoding) metadata));
            } else if (metadata instanceof Language) {
                clientInfo.getAcceptedLanguages().clear();
                clientInfo.getAcceptedLanguages().add(
                        new Preference<Language>((Language) metadata));
            } else if (metadata instanceof MediaType) {
                clientInfo.getAcceptedMediaTypes().clear();
                clientInfo.getAcceptedMediaTypes().add(
                        new Preference<MediaType>((MediaType) metadata));
            }
        }
    }

	
}
