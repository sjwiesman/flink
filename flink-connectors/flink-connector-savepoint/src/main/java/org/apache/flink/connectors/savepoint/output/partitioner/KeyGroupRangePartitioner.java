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

package org.apache.flink.connectors.savepoint.output.partitioner;

import org.apache.flink.annotation.Internal;
import org.apache.flink.api.common.functions.Partitioner;
import org.apache.flink.connectors.savepoint.output.metadata.SavepointMetadataProvider;
import org.apache.flink.runtime.state.KeyGroupRangeAssignment;

/**
 * A partitioner that selects the target channel based on the key group index.
 */
@Internal
public class KeyGroupRangePartitioner implements Partitioner<Integer> {
	private int maxParallelism = -1;

	private SavepointMetadataProvider provider;

	public KeyGroupRangePartitioner(SavepointMetadataProvider provider) {
		this.provider = provider;
	}

	@Override
	public int partition(Integer key, int numPartitions) {
		if (maxParallelism == -1 && provider != null) {
			maxParallelism = provider.maxParallelism();
		}

		return KeyGroupRangeAssignment.computeOperatorIndexForKeyGroup(
			maxParallelism,
			numPartitions,
			KeyGroupRangeAssignment.computeKeyGroupForKeyHash(key, maxParallelism));
	}
}

