/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.streaming.connectors.kafka.internals;

import org.apache.flink.metrics.Meter;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

/**
 * A source that tracks the rate of output records.
 * @param <T> Some output type.
 */
public class MeteredSource<T> implements SourceFunction.SourceContext<T> {

	private final SourceFunction.SourceContext<T> inner;

	private final Meter meter;

	public MeteredSource(SourceFunction.SourceContext<T> inner, Meter meter) {
		this.inner = inner;
		this.meter = meter;
	}

	@Override
	public void collect(T element) {
		meter.markEvent();
		inner.collect(element);
	}

	@Override
	public void collectWithTimestamp(T element, long timestamp) {
		meter.markEvent();
		inner.collectWithTimestamp(element, timestamp);
	}

	@Override
	public void emitWatermark(Watermark mark) {
		inner.emitWatermark(mark);
	}

	@Override
	public void markAsTemporarilyIdle() {
		inner.markAsTemporarilyIdle();
	}

	@Override
	public Object getCheckpointLock() {
		return inner.getCheckpointLock();
	}

	@Override
	public void close() {
		inner.close();
	}
}
