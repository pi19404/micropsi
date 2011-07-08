#pragma once

#ifndef GAMELIB_XMLUTILS_H_INCLUDED
#define GAMELIB_XMLUTILS_H_INCLUDED

#include "baselib/xmlutils.h"
#include "e42/core/ResourceHandles.h"

class CAnimationCtrl;

namespace XMLUtils
{
    void ReadAnimationsFromXML(TiXmlElement* pxAnimationsElement, const TModelHandle& hndModel, CAnimationCtrl* pAnimationCtrl);
};

#endif // GAMELIB_XMLUTILS_H_INCLUDED