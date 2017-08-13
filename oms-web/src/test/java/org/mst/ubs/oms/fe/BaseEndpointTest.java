package org.mst.ubs.oms.fe;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * AbstractEndpointTest with common test methods.
 */
public abstract class BaseEndpointTest {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected static final MediaType JSON_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("UTF-8"));
	protected static final MediaType XML_MEDIA_TYPE = new MediaType(MediaType.APPLICATION_XML.getType(), MediaType.APPLICATION_XML.getSubtype(), Charset.forName("UTF-8"));

	@Autowired
    protected WebApplicationContext webApplicationContext;

	@Autowired
	ObjectMapper objectMapper;
	
	protected MockMvc mockMvc;

	protected void setup() throws Exception {
		User user = new User("user","", AuthorityUtils.createAuthorityList("USER"));
		TestingAuthenticationToken testingAuthenticationToken = new TestingAuthenticationToken(user,null);


		this.mockMvc = webAppContextSetup(webApplicationContext)
				.apply(springSecurity())
				.build();
	}

	/**
	 * Returns json representation of the object.
	 * @param o instance
	 * @return json
	 * @throws IOException
	 */
	protected String json(Object o) throws IOException {

		return objectMapper.writeValueAsString(o);
	}


	protected TestingAuthenticationToken getPrincipalTestUser() {
		User user = new User("user","", AuthorityUtils.createAuthorityList("USER"));
		return  new TestingAuthenticationToken(user,null);
	}

}
