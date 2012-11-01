/*
 * Copyright (C) 2012 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dagger.internal.plugins.reflect;

import dagger.internal.Binding;
import dagger.internal.Keys;
import dagger.internal.Linker;
import dagger.internal.StaticInjection;
import java.lang.reflect.Field;

import checkers.nullness.quals.Nullable;

/**
 * Uses reflection to inject the static fields of a class.
 */
final class ReflectiveStaticInjection extends StaticInjection {
  private final Field[] fields;
  private @Nullable Binding<?>[] bindings;

  public ReflectiveStaticInjection(Field[] fields) {
    this.fields = fields;
  }

  @Override public void attach(Linker linker) {
    bindings = new Binding<?>[fields.length];
    for (int i = 0; i < fields.length; i++) {
      Field field = fields[i];
      String key = Keys.get(field.getGenericType(), field.getAnnotations(), field);
      bindings[i] = linker.requestBinding(key, field);
    }
  }

  @Override public void inject() {
    try {
      for (int f = 0; f < fields.length; f++) {
    	assert bindings[f] != null : "@SuppressWarnings(nullness)";
        fields[f].set(null, bindings[f].get());
      }
    } catch (IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }
}