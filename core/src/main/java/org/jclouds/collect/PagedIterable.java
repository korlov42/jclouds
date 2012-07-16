/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.collect;

import java.util.Iterator;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Allows you to advance through sequence of pages in a resultset. Typically
 * used in apis that return only a certain number of records at a time.
 * 
 * Simplest usage is to employ the {@link #concat} convenience function.
 * 
 * <pre>
 * FluentIterable<StorageMetadata> blobs = blobstore.list(...).concat();
 * for (StorageMetadata blob : blobs) {
 *     process(blob);
 * }
 * </pre>
 * 
 * Some may be interested in each page, for example to
 * 
 * <pre>
 * PagedIterator<StorageMetadata> blobs = blobstore.list(...).iterator();
 * while (blobs.hasNext()) {
 *     FluentIterable<StorageMetadata> page = blobs.next();
 *     ProcessedResults results = process(page);
 *     if (results.shouldBeBookmarked() && blobs.nextMarker().isPresent()) {
 *         saveBookmark(blobs.nextMarker().get());
 *     }
 * }
 * </pre>
 * 
 * @author Adrian Cole
 */
@Beta
public abstract class PagedIterable<E> extends FluentIterable<IterableWithMarker<E>> {

   @Override
   public abstract PagedIterator<E> iterator();

   /**
    * Combines all the pages into a single unmodifiable iterable. ex.
    * 
    * <pre>
    * FluentIterable<StorageMetadata> blobs = blobstore.list(...).concat();
    * for (StorageMetadata blob : blobs) {
    *     process(blob);
    * }
    * </pre>
    * 
    * @see Iterators#concat
    */
   public FluentIterable<E> concat() {
      final PagedIterator<E> iterator = iterator();
      final UnmodifiableIterator<Iterator<E>> unmodifiable = new UnmodifiableIterator<Iterator<E>>() {
         @Override
         public boolean hasNext() {
            return iterator.hasNext();
         }

         @Override
         public Iterator<E> next() {
            return iterator.next().iterator();
         }
      };
      return new FluentIterable<E>() {
         @Override
         public Iterator<E> iterator() {
            return Iterators.concat(unmodifiable);
         }
      };
   }

}