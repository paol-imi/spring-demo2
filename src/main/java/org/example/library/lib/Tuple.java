package org.example.library.lib;

/**
 * A tuple of two elements.
 *
 * @param <K> the type of the first element
 * @param <V> the type of the second element
 */
public record Tuple<K, V>(K key, V value) {
}
