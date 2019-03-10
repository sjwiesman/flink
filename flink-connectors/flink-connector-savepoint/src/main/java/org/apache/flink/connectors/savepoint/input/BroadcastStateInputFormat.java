/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.connectors.savepoint.input;

import org.apache.flink.annotation.PublicEvolving;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.runtime.state.OperatorStateBackend;

import java.util.Map;

/**
 * The input format for reading {@link org.apache.flink.api.common.state.BroadcastState}.
 *
 * @param <K> The type of the keys in the {@code BroadcastState}.
 * @param <V> The type of the values in the {@code BroadcastState}.
 */
@PublicEvolving
public class BroadcastStateInputFormat<K, V> extends OperatorStateInputFormat<Map.Entry<K, V>> {
	private final MapStateDescriptor<K, V> descriptor;

	/**
	 * Creates an input format for reading broadcast state from an operator in a savepoint.
	 *
	 * @param savepointPath The path to an existing savepoint.
	 * @param uid The uid of a particular operator.
	 * @param descriptor The descriptor for this state, providing a name and serializer.
	 */
	public BroadcastStateInputFormat(String savepointPath, String uid, MapStateDescriptor<K, V> descriptor) {
		super(savepointPath, uid, true);
		this.descriptor = descriptor;
	}

	@Override
	protected final Iterable<Map.Entry<K, V>> getElements(OperatorStateBackend restoredBackend) throws Exception {
		return restoredBackend.getBroadcastState(descriptor).entries();
	}
}
