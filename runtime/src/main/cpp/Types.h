#ifndef RUNTIME_TYPES_H
#define RUNTIME_TYPES_H

#include "Common.h"
#include "Memory.h"
#include "TypeInfo.h"

// Note that almost all types are signed.
typedef uint8_t KBoolean;
typedef int8_t  KByte;
typedef uint16_t KChar;
typedef int16_t KShort;
typedef int32_t KInt;
typedef int64_t KLong;
typedef float   KFloat;
typedef double  KDouble;

typedef ObjHeader* KRef;
typedef const ObjHeader* KConstRef;
typedef const ArrayHeader* KString;

#ifdef __cplusplus
extern "C" {
#endif

extern const TypeInfo* theAnyTypeInfo;
extern const TypeInfo* theCloneableTypeInfo;
extern const TypeInfo* theArrayTypeInfo;
extern const TypeInfo* theByteArrayTypeInfo;
extern const TypeInfo* theCharArrayTypeInfo;
extern const TypeInfo* theShortArrayTypeInfo;
extern const TypeInfo* theIntArrayTypeInfo;
extern const TypeInfo* theLongArrayTypeInfo;
extern const TypeInfo* theFloatArrayTypeInfo;
extern const TypeInfo* theDoubleArrayTypeInfo;
extern const TypeInfo* theBooleanArrayTypeInfo;
extern const TypeInfo* theStringTypeInfo;
extern const TypeInfo* theThrowableTypeInfo;

KBoolean IsInstance(const ObjHeader* obj, const TypeInfo* type_info) RUNTIME_PURE;
void CheckCast(const ObjHeader* obj, const TypeInfo* type_info);
KBoolean IsArray(KConstRef obj) RUNTIME_PURE;

typedef void (*Initializer)();
struct InitNode {
    Initializer      init;
    struct InitNode* next;
};

void AppendToInitializersTail(struct InitNode*);
void InitGlobalVariables();

#ifdef __cplusplus
}
#endif

#endif // RUNTIME_TYPES_H
