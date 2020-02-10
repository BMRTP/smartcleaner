#include "StateMachine.h"

StateMachine::StateMachine(int initialState) {
  this->currentState = initialState;
  this->externalInputHandler = nullptr;
}

void StateMachine::transition(int stateId) {
  this->currentState = stateId;
}

void StateMachine::update() {
  if(this->externalInputHandler != nullptr) {
    this->externalInputHandler();
  }
  this->delegates[this->currentState]();
}

void StateMachine::setState(int stateId, FunctionPointer delegate) {
  this->delegates[stateId] = delegate;
}
void StateMachine::setExernalInputHandler(FunctionPointer delegate) {
  this->externalInputHandler = delegate;
}
int StateMachine::getState() {
  return this->currentState;
}
