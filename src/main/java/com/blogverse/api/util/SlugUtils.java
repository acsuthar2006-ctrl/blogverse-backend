package com.blogverse.api.util;

import java.text.Normalizer;
import java.util.UUID;
import java.util.function.Function;

/**
 * Utility class for generating URL-friendly slugs from text.
 *
 * <p>Provides pure text-to-slug conversion as well as a convenience method
 * that appends a random suffix when a uniqueness check fails.
 *
 * <p>Usage examples:
 * <pre>{@code
 *   // Simple conversion
 *   SlugUtils.toSlug("Hello World!")  // → "hello-world"
 *
 *   // With uniqueness guarantee
 *   SlugUtils.toUniqueSlug("Hello World!", slug -> postRepository.findBySlug(slug).isEmpty())
 *   // → "hello-world" if unique, or "hello-world-a3f1b2" if not
 * }</pre>
 */
public final class SlugUtils {

	private SlugUtils() {
		// Utility class — prevent instantiation
	}

	/**
	 * Converts a text input into a URL-friendly slug.
	 *
	 * <p>Steps performed:
	 * <ol>
	 *   <li>Unicode normalization (NFD) and diacritic removal (é → e)</li>
	 *   <li>Lowercase conversion</li>
	 *   <li>Non-alphanumeric characters replaced with hyphens</li>
	 *   <li>Consecutive hyphens collapsed</li>
	 *   <li>Leading/trailing hyphens stripped</li>
	 * </ol>
	 *
	 * @param text the input text (e.g. a post title)
	 * @return the generated slug, or an empty string if the input is null/blank
	 */
	public static String toSlug(String text) {
		if (text == null || text.isBlank()) {
			return "";
		}

		return Normalizer.normalize(text, Normalizer.Form.NFD)
				.replaceAll("\\p{M}", "")            // strip diacritics
				.toLowerCase()
				.replaceAll("[^a-z0-9\\s-]", "")     // keep only alphanumeric, spaces, hyphens
				.replaceAll("[\\s-]+", "-")           // spaces & hyphens → single hyphen
				.replaceAll("^-|-$", "");             // trim leading/trailing hyphens
	}

	/**
	 * Generates a unique slug by appending a short UUID suffix when the base
	 * slug is already taken.
	 *
	 * @param text          the input text to slugify
	 * @param isAvailable   a function that returns {@code true} when the slug is not yet in use
	 * @return a slug guaranteed to satisfy {@code isAvailable}
	 */
	public static String toUniqueSlug(String text, Function<String, Boolean> isAvailable) {
		String slug = toSlug(text);

		if (isAvailable.apply(slug)) {
			return slug;
		}

		return slug + "-" + UUID.randomUUID().toString().substring(0, 6);
	}
}
