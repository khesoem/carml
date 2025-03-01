package com.taxonic.carml.rdfmapper;

import java.util.List;

public interface Combiner<T> {

  T combine(List<T> delegateInvocationResults);

}
