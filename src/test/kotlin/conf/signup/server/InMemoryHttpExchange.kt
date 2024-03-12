package conf.signup.server

import com.sun.net.httpserver.Headers
import java.io.ByteArrayInputStream

private fun noHeaders(): Headers =
    Headers()

private fun noBody(): ByteArrayInputStream =
    ByteArrayInputStream(ByteArray(0))

