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

package org.apache.flink.connectors.savepoint.runtime;

import org.apache.flink.runtime.checkpoint.CheckpointMetaData;
import org.apache.flink.runtime.checkpoint.CheckpointMetrics;
import org.apache.flink.runtime.checkpoint.PrioritizedOperatorSubtaskState;
import org.apache.flink.runtime.checkpoint.TaskStateSnapshot;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.runtime.state.LocalRecoveryConfig;
import org.apache.flink.runtime.state.LocalRecoveryDirectoryProvider;
import org.apache.flink.runtime.state.TaskStateManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A minimally implemented {@link TaskStateManager} that provides the functionality required to run
 * the {@code savepoint-connector}.
 */
final class SavepointTaskStateManager implements TaskStateManager {
	private static final String MSG = "This method should never be called";

	@Override
	public void reportTaskStateSnapshots(
		@Nonnull CheckpointMetaData checkpointMetaData,
		@Nonnull CheckpointMetrics checkpointMetrics,
		@Nullable TaskStateSnapshot acknowledgedState,
		@Nullable TaskStateSnapshot localState) {}

	@Nonnull
	@Override
	public PrioritizedOperatorSubtaskState prioritizedOperatorState(OperatorID operatorID) {
		return PrioritizedOperatorSubtaskState.emptyNotRestored();
	}

	@Nonnull
	@Override
	public LocalRecoveryConfig createLocalRecoveryConfig() {
		LocalRecoveryDirectoryProvider provider = new SavepointLocalRecoveryProvider();
		return new LocalRecoveryConfig(false, provider);
	}

	@Override
	public void notifyCheckpointComplete(long checkpointId) {
		throw new UnsupportedOperationException(MSG);
	}
}

