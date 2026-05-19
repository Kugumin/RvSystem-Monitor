#[macro_export]
macro_rules! jni_fn {
    (fn $name:ident($env:ident $(, $arg:ident: $type:ty)*) -> $ret:ty $body:block) => {
        #[unsafe(no_mangle)]
        pub extern "system" fn $name<'local>(
            mut unowned_env: jni::EnvUnowned<'local>,
            _class: jni::objects::JClass<'local>
            $(, $arg: $type)*
        ) -> $ret {
            unowned_env
                .with_env(|$env| {
                    let b = |$env: &mut jni::Env| -> jni::errors::Result<$ret> {
                        $body
                    };
                    b($env)
                })
                .resolve::<jni::errors::LogErrorAndDefault>()
        }
    };
}

#[macro_export]
macro_rules! jni_double_array {
    ($env:ident, $data:expr) => {{
        let data = $data;
        let output = $env.new_double_array(data.len())?;
        output.set_region($env, 0, &data)?;
        Ok(output.into_raw())
    }};
}

#[macro_export]
macro_rules! jni_long_array {
    ($env:ident, $data:expr) => {{
        let data = $data;
        let output = $env.new_long_array(data.len())?;
        output.set_region($env, 0, &data)?;
        Ok(output.into_raw())
    }};
}
