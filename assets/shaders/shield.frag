#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform sampler2D u_noise;
uniform float u_time;
uniform float u_health;

// --- new uniforms for impacts ---
#define MAX_IMPACTS 8
uniform int u_numImpacts; // how many are active this frame
uniform vec3 u_impacts[MAX_IMPACTS]; //.xy = uv pos,.z = age in seconds


varying vec4 v_color;
varying vec2 v_texCoord;

vec4 getTexture(vec2 offset) {
    return texture2D(u_texture, v_texCoord + offset);
}

float edge(vec4 original) {
    float edge = 0.;
    if (original.r > 0.) {
        float pix = 1./1000.;
        edge += 1. - getTexture(vec2(0, pix)).r;
        edge += 1. - getTexture(vec2(pix, pix)).r;
        edge += 1. - getTexture(vec2(pix, 0)).r;
        edge += 1. - getTexture(vec2(pix, -pix)).r;
        edge += 1. - getTexture(vec2(0, -pix)).r;
        edge += 1. - getTexture(vec2(-pix, -pix)).r;
        edge += 1. - getTexture(vec2(-pix, 0)).r;
        edge += 1. - getTexture(vec2(-pix, pix)).r;
        edge = clamp(edge, 0., 1.);
    }
    return edge;
}

void main() {

    vec2 np = vec2(v_texCoord.x + (u_time/10.5), v_texCoord.y + (u_time/10.5));
    vec2 np2 = vec2(v_texCoord.x - (u_time/10.), v_texCoord.y + (u_time/10.));
    vec2 np3 = vec2(v_texCoord.x - (u_time/9.), v_texCoord.y - (u_time/9.));
    vec2 np4 = vec2(v_texCoord.x + (u_time/8.), v_texCoord.y - (u_time/8.));

    vec4 noise = texture2D(u_noise, np) + texture2D(u_noise, np2) +texture2D(u_noise, np3) + texture2D(u_noise, np4) ;
    noise /= 4.;

    float mainStength = (.8 + (sin(u_time*3.) * .3)) * u_health * noise.g;
    float hexStrength = (.8 + sin(u_time*2.) * .2) * u_health * noise.g;

    // --- health-based colors ---
    vec3 healthyBack = vec3(.137,.463,.882); // blue
    vec3 healthyLine = vec3(.137,.663,.882); // light blue
    vec3 damagedBack = vec3(.882,.137,.137); // red
    vec3 damagedLine = vec3(.980,.463,.137); // orange-red

    // u_health: 1.0 = full health, 0.0 = dead
    float h = clamp(u_health, 0.0, 1.0);
    vec3 backColor = mix(damagedBack, healthyBack, h);
    vec3 lineColor = mix(damagedLine, healthyLine, h);

    vec4 texColor = getTexture(vec2(0.));

    vec3 shieldColor = mix(backColor, lineColor * hexStrength, texColor.g * .5);
    vec4 finalColor = vec4(texColor.r * shieldColor, texColor.b * mainStength);
//    gl_FragColor = vec4(edge(texColor));

    // --- add impact highlights ---
    float impactGlow = 0.0;
    for (int i = 0; i < MAX_IMPACTS; i++) {
        if (i >= u_numImpacts) continue;

        vec2 impactPos = u_impacts[i].xy;
        float age = u_impacts[i].z;

        // skip old impacts
        if (age <= 0.0 || age > 1.0) continue;

        float dist = distance(v_texCoord, impactPos);

        // expanding ring: radius grows with age, fade out
        float ringRadius = age * 0.1; // 0.4 = max ring size in UV space
        float ringWidth = 0.01;

        float d = abs(dist - ringRadius);
        float ring = 1.0 - smoothstep(0.0, ringWidth, d);

        // fade over 1 second
        float fade = 1.0 - smoothstep(0.0, 1.0, age);

        impactGlow += ring * fade;
    }

    // add bright white/blue flash
    impactGlow *= step(0.001, texColor.r);

    vec3 impactColor = mix(vec3(1.0, 0.3, 0.1), vec3(0.8, 0.9, 1.0), h);
    finalColor.rgb += impactColor * impactGlow * 2.0;
    finalColor.a = max(finalColor.a, impactGlow * texColor.r); // make sure it shows up


    finalColor = mix(finalColor, vec4(backColor, 1.), edge(texColor));
    gl_FragColor = finalColor * v_color;
}
