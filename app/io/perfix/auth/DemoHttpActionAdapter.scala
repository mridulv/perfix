package io.perfix.auth

import org.pac4j.core.context.{HttpConstants, WebContext}
import org.pac4j.core.exception.http.HttpAction
import org.pac4j.play.PlayWebContext
import org.pac4j.play.http.PlayHttpActionAdapter
import play.mvc.{Result, Results}

class DemoHttpActionAdapter extends PlayHttpActionAdapter {

  override def adapt(action: HttpAction, context: WebContext): Result = {
    val playWebContext = context.asInstanceOf[PlayWebContext];
    if (action != null && action.getCode == HttpConstants.UNAUTHORIZED) {
      playWebContext.supplementResponse(Results.forbidden("Unauthorized Access"))
    } else if (action != null && action.getCode == HttpConstants.FORBIDDEN) {
      playWebContext.supplementResponse(Results.forbidden("Forbidden Access"))
    } else {
      super.adapt(action, context)
    }
  }
}
