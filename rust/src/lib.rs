//! # RvSystem Monitor Rust Backend
//!
//! This crate provides the native implementation for system monitoring tasks in the RvSystem Monitor application.
//! It interfaces with the Android application via JNI (Java Native Interface).

#![allow(non_snake_case)]

use jni::objects::JString;
use jni::strings::JNIString;
use jni::sys::{jdouble, jdoubleArray, jint, jlong, jlongArray, jobjectArray, jstring};

pub mod drivers;
pub mod kernel;
pub mod macros;
pub mod mm;

jni_fn! {
    fn Java_com_rve_systemmonitor_utils_DeviceUtils_getRustLibraryVersionNative(env) -> jstring {
        let version = env!("CARGO_PKG_VERSION");
        Ok(env.new_string(version)?.into_raw())
    }
}

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

        jni_double_array!(env, data)
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

        jni_double_array!(env, data)
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

        jni_double_array!(env, data)
    }
}

jni_fn! {
    fn Java_com_rve_systemmonitor_utils_CpuUtils_getStaticCoreInfoNative(env) -> jlongArray {
        let cores = kernel::cpu::get_core_count();
        let mut data = Vec::with_capacity(cores as usize * 2);

        for i in 0..cores {
            data.push(kernel::cpu::get_core_frequency(i, "min_info"));
            data.push(kernel::cpu::get_core_frequency(i, "max_info"));
        }

        jni_long_array!(env, data)
    }
}

jni_fn! {
    fn Java_com_rve_systemmonitor_utils_CpuUtils_getAllCoreGovernorsNative(env) -> jobjectArray {
        let cores = kernel::cpu::get_core_count();
        let first_gov = kernel::cpu::get_core_governor(0);
        let initial_element = env.new_string(first_gov)?;

        let class = env.find_class(JNIString::from("java/lang/String"))?;
        let array = env.new_object_array(cores, &class, initial_element)?;

        for i in 1..cores {
            let governor = kernel::cpu::get_core_governor(i);
            let s = env.new_string(governor)?;
            array.set_element(env, i as usize, s)?;
        }

        Ok(array.into_raw())
    }
}

jni_fn! {
    fn Java_com_rve_systemmonitor_utils_CpuUtils_getAllCoreFrequenciesNative(env) -> jlongArray {
        let cores = kernel::cpu::get_core_count();
        let mut frequencies = Vec::with_capacity(cores as usize);

        for i in 0..cores {
            frequencies.push(kernel::cpu::get_core_frequency(i, "cur"));
        }

        jni_long_array!(env, frequencies)
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

        jni_double_array!(env, temps)
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

        jni_double_array!(env, data)
    }
}

jni_fn! {
    fn Java_com_rve_systemmonitor_utils_CpuUtils_calculateCpuLoadNative(env, proc_stat: JString<'local>) -> jdoubleArray {
        let proc_stat: String = proc_stat.try_to_string(env)?;
        let results = kernel::cpu::calculate_cpu_load(&proc_stat);
        jni_double_array!(env, results)
    }
}
