package com.github.kittinunf.fuel.core

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.executors.CancellableRequest
import com.github.kittinunf.fuel.core.executors.RequestExecutor
import com.github.kittinunf.fuel.core.executors.RequestOptions
import com.github.kittinunf.fuel.core.requests.DownloadDestinationCallback
import com.github.kittinunf.fuel.core.requests.DownloadRequest
import com.github.kittinunf.fuel.core.requests.MultipartRequest
import com.github.kittinunf.result.Result
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.charset.Charset

typealias Parameters = List<Pair<String, Any?>>

interface Request : RequestOptions, RequestExecutions, RequestRepresentation, RequestAuthentication, Fuel.RequestConvertible {
    val method: Method
    val url: URL
    val parameters: Parameters
    var executor: RequestExecutor

    val headers: Headers
    operator fun get(header: String): HeaderValues
    operator fun set(header: String, values: Collection<*>): Request
    operator fun set(header: String, value: Any): Request

    fun header(header: String): HeaderValues
    fun header(map: Map<String, Any>): Request
    fun header(vararg pairs: Pair<String, Any>): Request
    fun header(header: String, values: Collection<*>): Request
    fun header(header: String, value: Any): Request
    fun header(header: String, vararg values: Any): Request
    fun appendHeader(header: String, value: Any): Request
    fun appendHeader(header: String, vararg values: Any): Request
    fun appendHeader(vararg pairs: Pair<String, Any>): Request

    var body: Body
    fun body(openStream: BodySource, calculateLength: BodyLength? = null, charset: Charset = Charsets.UTF_8): Request
    fun body(stream: InputStream, calculateLength: BodyLength? = null, charset: Charset = Charsets.UTF_8): Request
    fun body(bytes: ByteArray, charset: Charset = Charsets.UTF_8): Request
    fun body(body: String, charset: Charset = Charsets.UTF_8): Request
    fun body(file: File, charset: Charset = Charsets.UTF_8): Request
    fun jsonBody(body: String, charset: Charset = Charsets.UTF_8): Request

    val requestProgress: Progress
    val responseProgress: Progress
    fun responseProgress(handler: ProgressCallback): Request
    fun responseProgress(handlers: Progress): Request
    fun requestProgress(handler: ProgressCallback): Request
    fun requestProgress(handlers: Progress): Request

    fun multipart(): MultipartRequest
    fun download(): DownloadRequest
    fun destination(destination: DownloadDestinationCallback): DownloadRequest
}

interface RequestExecutions {
    fun response(handler: HandlerWithResult<ByteArray>): CancellableRequest
    fun response(handler: Handler<ByteArray>): CancellableRequest
    fun response(): Triple<Request, Response, Result<ByteArray, FuelError>>

    fun responseString(charset: Charset, handler: HandlerWithResult<String>): CancellableRequest
    fun responseString(handler: HandlerWithResult<String>): CancellableRequest
    fun responseString(charset: Charset, handler: Handler<String>): CancellableRequest
    fun responseString(handler: Handler<String>): CancellableRequest
    fun responseString(charset: Charset): Triple<Request, Response, Result<String, FuelError>>
    fun responseString(): Triple<Request, Response, Result<String, FuelError>>

    fun <T : Any> responseObject(deserializer: ResponseDeserializable<T>, handler: HandlerWithResult<T>): CancellableRequest
    fun <T : Any> responseObject(deserializer: ResponseDeserializable<T>, handler: Handler<T>): CancellableRequest
    fun <T : Any> responseObject(deserializer: ResponseDeserializable<T>): Triple<Request, Response, Result<T, FuelError>>
}

interface RequestRepresentation {
    override fun toString(): String
    fun httpString(): String
    fun cUrlString(): String
}

interface RequestAuthentication {
    fun authenticate(username: String, password: String): Request
    fun basicAuthentication(username: String, password: String): Request
    fun bearerAuthentication(token: String): Request
}