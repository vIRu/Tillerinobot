package tillerino.tillerinobot.rest;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.slf4j.MDC;

import lombok.RequiredArgsConstructor;
import tillerino.tillerinobot.BotBackend;

@KeyRequired
@Priority(Priorities.AUTHENTICATION)
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class AuthenticationFilter implements ContainerRequestFilter {
	private final BotBackend backend;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String apiKey = Optional.ofNullable(requestContext.getUriInfo().getQueryParameters().get("k"))
				.flatMap(l -> l.stream().findFirst()).orElse(requestContext.getHeaderString("k"));

		try {
			if (apiKey == null || !backend.verifyGeneralKey(apiKey)) {
				throw new WebApplicationException("Please provide an API key", 401);
			}
		} catch (SQLException e) {
			throw new InternalServerErrorException(e);
		}
		MDC.put("apiKey", apiKey);
	}
}
