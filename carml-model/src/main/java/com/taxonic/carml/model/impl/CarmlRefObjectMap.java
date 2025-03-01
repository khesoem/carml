package com.taxonic.carml.model.impl;

import com.google.common.collect.ImmutableSet;
import com.taxonic.carml.model.Join;
import com.taxonic.carml.model.RefObjectMap;
import com.taxonic.carml.model.Resource;
import com.taxonic.carml.model.TriplesMap;
import com.taxonic.carml.rdfmapper.annotations.RdfProperty;
import com.taxonic.carml.rdfmapper.annotations.RdfType;
import com.taxonic.carml.vocab.Carml;
import com.taxonic.carml.vocab.Rdf;
import com.taxonic.carml.vocab.Rr;
import java.util.Set;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.builder.MultilineRecursiveToStringStyle;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;

@SuperBuilder
@NoArgsConstructor
public class CarmlRefObjectMap extends CarmlResource implements RefObjectMap {

  @Setter
  private TriplesMap parentTriplesMap;

  @Singular
  @Setter
  private Set<Join> joinConditions;

  @RdfProperty(Rr.parentTriplesMap)
  @RdfType(CarmlTriplesMap.class)
  @Override
  public TriplesMap getParentTriplesMap() {
    return parentTriplesMap;
  }

  @RdfProperty(Rr.joinCondition)
  @RdfProperty(value = Carml.multiJoinCondition, deprecated = true)
  @RdfType(CarmlJoin.class)
  @Override
  public Set<Join> getJoinConditions() {
    return joinConditions;
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, new MultilineRecursiveToStringStyle()).toString();
  }

  @Override
  public Set<Resource> getReferencedResources() {
    ImmutableSet.Builder<Resource> builder = ImmutableSet.<Resource>builder();
    if (parentTriplesMap != null) {
      builder.add(parentTriplesMap);
    }
    return builder.addAll(joinConditions)
        .build();
  }

  @Override
  public void addTriples(ModelBuilder modelBuilder) {
    modelBuilder.subject(getAsResource())
        .add(RDF.TYPE, Rdf.Rr.RefObjectMap);
    if (parentTriplesMap != null) {
      modelBuilder.add(Rr.parentTriplesMap, parentTriplesMap.getAsResource());
    }
    joinConditions.forEach(jc -> modelBuilder.add(Rr.joinCondition, jc.getAsResource()));
  }
}
