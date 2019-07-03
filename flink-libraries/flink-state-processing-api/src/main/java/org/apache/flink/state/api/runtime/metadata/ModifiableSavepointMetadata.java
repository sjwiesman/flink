package org.apache.flink.state.api.runtime.metadata;

import org.apache.flink.annotation.Internal;
import org.apache.flink.runtime.checkpoint.MasterState;
import org.apache.flink.runtime.checkpoint.OperatorState;
import org.apache.flink.runtime.jobgraph.OperatorID;
import org.apache.flink.state.api.BootstrapTransformation;
import org.apache.flink.state.api.runtime.BootstrapTransformationWithID;
import org.apache.flink.state.api.runtime.OperatorIDGenerator;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Savepoint metadata that can be modified.
 */
@Internal
public class ModifiableSavepointMetadata extends SavepointMetadata {

	private transient Map<OperatorID, OperatorStateSpec> operatorStateIndex;

	public ModifiableSavepointMetadata(int maxParallelism, Collection<MasterState> masterStates, Collection<OperatorState> initialStates) {
		super(maxParallelism, masterStates);

		this.operatorStateIndex = new HashMap<>(initialStates.size());
		initialStates.forEach(existingState -> operatorStateIndex.put(
			existingState.getOperatorID(),
			OperatorStateSpec.existing(existingState)));
	}

	/**
	 * @return Operator state for the given UID.
	 *
	 * @throws IOException If the savepoint does not contain operator state with the given uid.
	 */
	public OperatorState getOperatorState(String uid) throws IOException {
		OperatorID operatorID = OperatorIDGenerator.fromUid(uid);

		OperatorStateSpec operatorState = operatorStateIndex.get(operatorID);
		if (operatorState == null || operatorState.isNewStateTransformation()) {
			throw new IOException("Savepoint does not contain state with operator uid " + uid);
		}

		return operatorState.asExistingState();
	}

	public void removeOperator(String uid) {
		operatorStateIndex.remove(OperatorIDGenerator.fromUid(uid));
	}

	public void addOperator(String uid, BootstrapTransformation<?> transformation) {
		OperatorID id = OperatorIDGenerator.fromUid(uid);

		if (operatorStateIndex.containsKey(id)) {
			throw new IllegalArgumentException("The savepoint already contains uid " + uid + ". All uid's must be unique");
		}

		operatorStateIndex.put(id, OperatorStateSpec.newWithTransformation(new BootstrapTransformationWithID<>(id, transformation)));
	}

	/**
	 * @return List of {@link OperatorState} that already exists within the savepoint.
	 */
	public List<OperatorState> getExistingOperators() {
		return operatorStateIndex
			.values()
			.stream()
			.filter(OperatorStateSpec::isExistingState)
			.map(OperatorStateSpec::asExistingState)
			.collect(Collectors.toList());
	}

	/**
	 * @return List of new operator states for the savepoint, represented by their target {@link OperatorID} and {@link BootstrapTransformation}.
	 */
	public List<BootstrapTransformationWithID<?>> getNewOperators() {
		return operatorStateIndex
			.values()
			.stream()
			.filter(OperatorStateSpec::isNewStateTransformation)
			.map(OperatorStateSpec::asNewStateTransformation)
			.collect(Collectors.toList());
	}
}
