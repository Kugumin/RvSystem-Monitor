//! # RvSystem Monitor Rust Backend
//!
//! This crate provides the native implementation for system monitoring tasks in the RvSystem Monitor application.
//! It interfaces with the Android application via JNI (Java Native Interface).

#![allow(non_snake_case)]

use jni::objects::JString;
use jni::sys::{jdouble, jdoubleArray, jint, jlong, jlongArray, jstring};

pub mod drivers;
pub mod kernel;
pub mod macros;
pub mod mm;

jni_fn! {
    fn Java_com_rve_systemmonitor_utils_GpuUtils_getVulkanVersionNative(env) -> jstring {
        let version = drivers::gpu::vulkan::get_vulkan_version();
        Ok(env.new_string(version)?.into_raw())
    }
}

jni_fn! {
    fn Java_com_rve_systemmonitor_utils_GpuUtils_getGpuTemperatureNative(env) -> jdouble {
        let _ = env;
        Ok(kernel::cpu::get_gpu_temperature())
    }
}

jni_fn! {
    fn Java_com_rve_systemmonitor_utils_MemoryUtils_getMemoryDataNative(env) -> jdoubleArray {
        let (ram, zram) = mm::memory::get_memory_data();

        let is_active = if zram.is_active { 1.0 } else { 0.0 };
        let data = [
            ram.total,
            ram.available,
            ram.used,
            ram.used_percentage,
            ram.cached,
            ram.buffers,
            ram.active,
            ram.inactive,
            ram.slab,
            is_active,
            zram.total,
            zram.available,
            zram.used,
            zram.used_percentage,
        ];

        let output = env.new_double_array(14)?;
        output.set_region(env, 0, &data)?;
        Ok(output.into_raw())
    }
}

jni_fn! {
    fn Java_com_rve_systemmonitor_utils_MemoryUtils_getRamDataNative(env) -> jdoubleArray {
        let (ram, _) = mm::memory::get_memory_data();

        let data = [
            ram.total,
            ram.available,
            ram.used,
            ram.used_percentage,
            ram.cached,
            ram.buffers,
            ram.active,
            ram.inactive,
            ram.slab,
        ];

        let output = env.new_double_array(9)?;
        output.set_region(env, 0, &data)?;
        Ok(output.into_raw())
    }
}

jni_fn! {
    fn Java_com_rve_systemmonitor_utils_MemoryUtils_getZramDataNative(env) -> jdoubleArray {
        let (_, zram) = mm::memory::get_memory_data();

        let is_active = if zram.is_active { 1.0 } else { 0.0 };
        let data = [
            is_active,
            zram.total,
            zram.available,
            zram.used,
            zram.used_percentage,
        ];

        let output = env.new_double_array(5)?;
        output.set_region(env, 0, &data)?;
        Ok(output.into_raw())
    }
}

jni_fn! {
    fn Java_com_rve_systemmonitor_utils_CpuUtils_getAllCoreFrequenciesNative(env) -> jlongArray {
        let cores = kernel::cpu::get_core_count();
        let mut frequencies = Vec::with_capacity(cores as usize);

        for i in 0..cores {
            frequencies.push(kernel::cpu::get_core_frequency(i, "cur"));
        }

        let output = env.new_long_array(cores as usize)?;
        output.set_region(env, 0, &frequencies)?;
        Ok(output.into_raw())
    }
}

jni_fn! {
    fn Java_com_rve_systemmonitor_utils_CpuUtils_getCoreCountNative(env) -> jint {
        let _ = env;
        Ok(kernel::cpu::get_core_count())
    }
}

jni_fn! {
    fn Java_com_rve_systemmonitor_utils_CpuUtils_getCoreFrequencyNative(env, core_id: jint, freq_type: JString<'local>) -> jlong {
        let freq_type: String = freq_type.try_to_string(env)?;
        Ok(kernel::cpu::get_core_frequency(core_id, &freq_type))
    }
}

jni_fn! {
    fn Java_com_rve_systemmonitor_utils_CpuUtils_getCoreGovernorNative(env, core_id: jint) -> jstring {
        let governor = kernel::cpu::get_core_governor(core_id);
        Ok(env.new_string(governor)?.into_raw())
    }
}

jni_fn! {
    fn Java_com_rve_systemmonitor_utils_CpuUtils_getCpuTemperatureNative(env) -> jdouble {
        let _ = env;
        Ok(kernel::cpu::get_cpu_temperature())
    }
}

jni_fn! {
    fn Java_com_rve_systemmonitor_utils_CpuUtils_getAllCoreTemperaturesNative(env) -> jdoubleArray {
        let cores = kernel::cpu::get_core_count();
        let mut temps = Vec::with_capacity(cores as usize);

        for i in 0..cores {
            temps.push(kernel::cpu::get_core_temperature(i));
        }

        let output = env.new_double_array(cores as usize)?;
        output.set_region(env, 0, &temps)?;
        Ok(output.into_raw())
    }
}

jni_fn! {
    fn Java_com_rve_systemmonitor_utils_CpuUtils_getCpuDynamicDataNative(env) -> jdoubleArray {
        let cores = kernel::cpu::get_core_count() as usize;
        let mut data = Vec::with_capacity(1 + 2 * cores);

        data.push(kernel::cpu::get_cpu_temperature());

        for i in 0..cores {
            data.push(kernel::cpu::get_core_frequency(i as i32, "cur") as f64);
            data.push(kernel::cpu::get_core_temperature(i as i32));
        }

        let output = env.new_double_array(data.len())?;
        output.set_region(env, 0, &data)?;
        Ok(output.into_raw())
    }
}
