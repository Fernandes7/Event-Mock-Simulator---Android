package com.example.eventtrackersimulator.common

import org.json.JSONArray
import org.json.JSONObject

object JsonUtils {

    /** A single decoded item from a `trackBatch` JSON array: `{"eventType": "...", "payload": {...}}`. */
    data class BatchItem(val eventType: String, val payload: Map<String, String>)

    /** Serializes a flat string-to-string payload map to a JSON object string, e.g. `{"sku":"123"}`. */
    fun Map<String, String>.toJsonString(): String {
        val json = JSONObject()
        forEach { (key, value) -> json.put(key, value) }
        return json.toString()
    }

    /** Parses a JSON object string back into a flat string map. Empty map for blank/invalid input. */
    fun String.toPayloadMap(): Map<String, String> {
        if (isBlank()) return emptyMap()
        val json = JSONObject(this)
        return json.keys().asSequence().associateWith { key -> json.getString(key) }
    }

    /**
     * Parses the JSON array string accepted by `EventTracker.trackBatch`, e.g.
     * `[{"eventType":"VISIT","payload":{"screen":"home"}}, ...]`.
     */
    fun parseEventBatch(jsonArrayString: String): List<BatchItem> {
        val array = JSONArray(jsonArrayString)
        return (0 until array.length()).map { index ->
            val obj = array.getJSONObject(index)
            val payloadJson = obj.optJSONObject("payload")
            val payload = payloadJson?.let { p ->
                p.keys().asSequence().associateWith { key -> p.getString(key) }
            } ?: emptyMap()
            BatchItem(eventType = obj.getString("eventType"), payload = payload)
        }
    }
}
