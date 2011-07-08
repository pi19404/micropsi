//----------------------------------------------------------------------------
// std-shader für Objekte ohne Skinning
//----------------------------------------------------------------------------

float4	c_vLightDir = {1.0f, 0.0f, 0.0f, 1.0f};

// Matrices
float4x3    c_mWorld			: WORLD;
float4x4    c_mWorldViewProj	: WORLDVIEWPROJECTION;

//----------------------------------------------------------------------------
struct VertPN
{
    float3  vPos             : POSITION;
    float3  vNormal          : NORMAL;
};

struct VertP
{
    float4  vPos     : POSITION;
};

//----------------------------------------------------------------------------
float3 
CalcHue(float3 vNormal)
{
	return dot(vNormal, -c_vLightDir.xyz) * 0.5f + 0.5f;
}
//----------------------------------------------------------------------------
VertP
VShade(VertP i)
{
    VertP   o;
     
	o.vPos = mul(float4(i.vPos.xyz, 1.0f), c_mWorldViewProj);
    
    return o;
}
//----------------------------------------------------------------------------
VertexShader vsShader = compile vs_1_1 VShade();
//----------------------------------------------------------------------------
technique _staticmesh
{
    pass p0
    {
    	CullMode = CW;

        VertexShader = (vsShader);

		AlphaTestEnable = false;
		ZEnable = true;

        ColorOp[0] = SelectArg1;
        ColorArg1[0] = TFactor;

        ColorOp[1] = Disable;
        

        AlphaOp[0] = SelectArg1;
        AlphaArg1[0] = TFactor;

        AlphaOp[1] = Disable;


		AddressU[0] = Wrap;
		AddressV[0] = Wrap;

		AlphaBlendEnable = false;
    }
}
//----------------------------------------------------------------------------
