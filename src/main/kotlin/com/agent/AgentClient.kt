// Agent typed client SDK for Kotlin/JVM (agent.v1.AgentService).
//
// Pure Connect client — no easylab gateway, no REST. The typed RPC client is
// generated from agent/v1/agent.proto by buf; this package adds a ProtocolClient
// (OkHttp) with bearer auth and packs the agent.v1.AgentService surface.
//
// Usage:
//   val client = AgentClient(baseUrl = "https://agent.example.com", token = "...")
//   val sessions = client.listSessions() // suspend
package com.agent

import com.connectrpc.ProtocolClientConfig
import com.connectrpc.getOrThrow
import com.connectrpc.impl.ProtocolClient
import com.connectrpc.extensions.GoogleJavaLiteProtobufStrategy
import com.connectrpc.http.clone
import com.connectrpc.Interceptor
import com.connectrpc.StreamFunction
import com.connectrpc.UnaryFunction
import com.connectrpc.okhttp.ConnectOkHttpClient
import com.connectrpc.protocols.NetworkProtocol
import com.agent.v1.AgentServiceClient
import com.agent.v1.AgentServiceClientInterface
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient

/** Injects an `Authorization: Bearer <token>` header on every request. */
class AgentBearerInterceptor(private val token: String) : Interceptor {
    override fun unaryFunction(): UnaryFunction {
        return UnaryFunction(
            requestFunction = { request ->
                val headers = request.headers.toMutableMap()
                headers["Authorization"] = listOf("Bearer $token")
                request.clone(headers = headers)
            },
            responseFunction = { response -> response },
        )
    }

    override fun streamFunction(): StreamFunction {
        return StreamFunction(
            requestFunction = { request ->
                val headers = request.headers.toMutableMap()
                headers["Authorization"] = listOf("Bearer $token")
                request.clone(headers = headers)
            },
        )
    }
}

/** The typed agent client over agent.v1.AgentService. */
class AgentClient(
    baseUrl: String,
    token: String,
    okHttp: OkHttpClient = OkHttpClient(),
) {
    private val host = baseUrl.trimEnd('/')

    private val protocolClient = ProtocolClient(
        httpClient = ConnectOkHttpClient(okHttp),
        ProtocolClientConfig(
            host = host,
            serializationStrategy = GoogleJavaLiteProtobufStrategy(),
            networkProtocol = NetworkProtocol.CONNECT,
            ioCoroutineContext = Dispatchers.IO,
            interceptors = listOf({ _: ProtocolClientConfig -> AgentBearerInterceptor(token) }),
        ),
    )

    val agent: AgentServiceClientInterface = AgentServiceClient(protocolClient)

    /** List sessions. */
    suspend fun listSessions(): List<com.agent.v1.Session> {
        val r = agent.listSessions(com.agent.v1.listSessionsRequest { })
        return r.getOrThrow().sessionsList
    }
}
