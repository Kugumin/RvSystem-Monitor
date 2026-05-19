//! # Memory Data Provider
//!
//! Provides functions to read and parse memory information from the system.

use std::fs::File;
use std::io::{self, BufRead};

/// Factor used to convert bytes to Gigabytes.
const GB_FACTOR: f64 = 1_000_000_000.0;

/// Represents RAM usage data.
#[derive(Debug, Default)]
pub struct RamData {
    /// Total RAM in GB.
    pub total: f64,
    /// Available RAM in GB.
    pub available: f64,
    /// Used RAM in GB.
    pub used: f64,
    /// Percentage of RAM used.
    pub used_percentage: f64,
    /// Cached RAM in GB.
    pub cached: f64,
    /// Buffers in GB.
    pub buffers: f64,
    /// Active RAM in GB.
    pub active: f64,
    /// Inactive RAM in GB.
    pub inactive: f64,
    /// Slab in GB.
    pub slab: f64,
}

/// Represents ZRAM (Compressed RAM) usage data.
#[derive(Debug, Default)]
pub struct ZramData {
    /// Indicates if ZRAM is currently active.
    pub is_active: bool,
    /// Total ZRAM in GB.
    pub total: f64,
    /// Available ZRAM in GB.
    pub available: f64,
    /// Used ZRAM in GB.
    pub used: f64,
    /// Percentage of ZRAM used.
    pub used_percentage: f64,
}

/// Formats a float to two decimal places.
fn format_to_two_decimals(value: f64) -> f64 {
    (value * 100.0).round() / 100.0
}

/// Formats a float from bytes to GB with two decimal places.
fn to_gb_formatted(bytes: f64) -> f64 {
    format_to_two_decimals(bytes / GB_FACTOR)
}

/// Helper to parse the KB value from the rest of a meminfo line without extra allocations
fn parse_kb(rest: &str) -> f64 {
    rest.split_whitespace()
        .next()
        .and_then(|s| s.parse::<f64>().ok())
        .unwrap_or(0.0)
}

/// Retrieves memory data by parsing `/proc/meminfo`.
///
/// Returns a tuple containing `RamData` and `ZramData`.
pub fn get_memory_data() -> (RamData, ZramData) {
    let mut mem_total_bytes = 0_f64;
    let mut mem_available_bytes = 0_f64;
    let mut swap_total_bytes = 0_f64;
    let mut swap_free_bytes = 0_f64;
    let mut cached_bytes = 0_f64;
    let mut buffers_bytes = 0_f64;
    let mut active_bytes = 0_f64;
    let mut inactive_bytes = 0_f64;
    let mut slab_bytes = 0_f64;

    if let Ok(file) = File::open("/proc/meminfo") {
        let mut reader = io::BufReader::new(file);
        let mut line = String::with_capacity(64);

        while reader.read_line(&mut line).unwrap_or(0) > 0 {
            if let Some((key, rest)) = line.split_once(':') {
                let bytes = parse_kb(rest) * 1024.0;
                match key {
                    "MemTotal" => mem_total_bytes = bytes,
                    "MemAvailable" => mem_available_bytes = bytes,
                    "SwapTotal" => swap_total_bytes = bytes,
                    "SwapFree" => swap_free_bytes = bytes,
                    "Cached" => cached_bytes = bytes,
                    "Buffers" => buffers_bytes = bytes,
                    "Active" => active_bytes = bytes,
                    "Inactive" => inactive_bytes = bytes,
                    "Slab" => slab_bytes = bytes,
                    _ => {}
                }
            }
            line.clear();
        }
    } else {
        println!("Failed to read /proc/meminfo");
    }

    let ram_used_bytes = mem_total_bytes - mem_available_bytes;
    let ram_percentage = if mem_total_bytes > 0.0 {
        (ram_used_bytes / mem_total_bytes) * 100.0
    } else {
        0.0
    };

    let ram_data = RamData {
        total: to_gb_formatted(mem_total_bytes),
        available: to_gb_formatted(mem_available_bytes),
        used: to_gb_formatted(ram_used_bytes),
        used_percentage: format_to_two_decimals(ram_percentage),
        cached: to_gb_formatted(cached_bytes),
        buffers: to_gb_formatted(buffers_bytes),
        active: to_gb_formatted(active_bytes),
        inactive: to_gb_formatted(inactive_bytes),
        slab: to_gb_formatted(slab_bytes),
    };

    let swap_used_bytes = swap_total_bytes - swap_free_bytes;
    let swap_percentage = if swap_total_bytes > 0.0 {
        (swap_used_bytes / swap_total_bytes) * 100.0
    } else {
        0.0
    };

    let zram_data = ZramData {
        is_active: swap_total_bytes > 0.0,
        total: to_gb_formatted(swap_total_bytes),
        available: to_gb_formatted(swap_free_bytes),
        used: to_gb_formatted(swap_used_bytes),
        used_percentage: format_to_two_decimals(swap_percentage),
    };

    (ram_data, zram_data)
}
