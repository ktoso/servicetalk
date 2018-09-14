/*
 * Copyright © 2018 Apple Inc. and the ServiceTalk project authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.servicetalk.http.api;

import io.servicetalk.transport.api.ExecutionContext;

import static java.util.Objects.requireNonNull;

/**
 * The equivalent of {@link StreamingHttpRequester} but with synchronous/blocking APIs instead of asynchronous APIs.
 */
public abstract class BlockingStreamingHttpRequester implements BlockingStreamingHttpRequestFactory, AutoCloseable {
    private final BlockingStreamingHttpRequestFactory requestFactory;

    /**
     * Create a new instance.
     * @param requestFactory The {@link HttpRequestFactory} used to
     * {@link #newRequest(HttpRequestMethod, String) create new requests}.
     */
    protected BlockingStreamingHttpRequester(BlockingStreamingHttpRequestFactory requestFactory) {
        this.requestFactory = requireNonNull(requestFactory);
    }

    /**
     * Send a {@code request}.
     *
     * @param request the request to send.
     * @return The response.
     * @throws Exception if an exception occurs during the request processing.
     */
    public abstract BlockingStreamingHttpResponse request(BlockingStreamingHttpRequest request) throws Exception;

    /**
     * Get the {@link ExecutionContext} used during construction of this object.
     * <p>
     * Note that the {@link ExecutionContext#getIoExecutor()} will not necessarily be associated with a specific thread
     * unless that was how this object was built.
     *
     * @return the {@link ExecutionContext} used during construction of this object.
     */
    public abstract ExecutionContext getExecutionContext();

    @Override
    public final BlockingStreamingHttpRequest newRequest(HttpRequestMethod method, String requestTarget) {
        return requestFactory.newRequest(method, requestTarget);
    }

    @Override
    public final BlockingStreamingHttpResponseFactory getHttpResponseFactory() {
        return requestFactory.getHttpResponseFactory();
    }

    /**
     * Convert this {@link BlockingStreamingHttpRequester} to the {@link StreamingHttpRequester} API.
     * <p>
     * Note that the resulting {@link StreamingHttpRequester} may still be subject to any blocking, in memory
     * aggregation, and other behavior as this {@link BlockingStreamingHttpRequester}.
     *
     * @return a {@link StreamingHttpRequester} representation of this {@link BlockingStreamingHttpRequester}.
     */
    public final StreamingHttpRequester asStreamingRequester() {
        return asStreamingRequesterInternal();
    }

    /**
     * Convert this {@link BlockingStreamingHttpRequester} to the {@link HttpRequester} API.
     * <p>
     * Note that the resulting {@link HttpRequester} may still be subject to any blocking, in memory
     * aggregation, and other behavior as this {@link BlockingStreamingHttpRequester}.
     *
     * @return a {@link HttpRequester} representation of this {@link BlockingStreamingHttpRequester}.
     */
    public final HttpRequester asRequester() {
        return asStreamingRequester().asRequester();
    }

    /**
     * Convert this {@link BlockingStreamingHttpRequester} to the {@link BlockingHttpRequester} API.
     * <p>
     * Note that the resulting {@link BlockingHttpRequester} may still be subject to in memory
     * aggregation and other behavior as this {@link BlockingStreamingHttpRequester}.
     *
     * @return a {@link BlockingHttpRequester} representation of this {@link BlockingStreamingHttpRequester}.
     */
    public final BlockingHttpRequester asBlockingRequester() {
        return asStreamingRequester().asBlockingRequester();
    }

    StreamingHttpRequester asStreamingRequesterInternal() {
        return new BlockingStreamingHttpRequesterToStreamingHttpRequester(this);
    }
}