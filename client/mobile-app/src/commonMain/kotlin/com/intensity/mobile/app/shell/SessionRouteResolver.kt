package com.intensity.mobile.app.shell

import com.intensity.mobile.app.shell.session.AppSession

enum class SessionRoute {
    CONNECT,
    CURATE,
    UNKNOWN
}

class SessionRouteResolver {
    fun resolve(session: AppSession): SessionRoute {
        return when (session.accessMode) {
            "CONNECT" -> SessionRoute.CONNECT
            "CURATE" -> SessionRoute.CURATE
            else -> SessionRoute.UNKNOWN
        }
    }
}
