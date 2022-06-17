/*
 * Copyright 2010-2022 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#if KONAN_HAS_UIKIT_FRAMEWORK

#include "AppStateTracking.hpp"

#include <functional>

#import <Foundation/NSNotification.h>

#include "CompilerConstants.hpp"
#include "objc_support/NSNotificationSubscription.hpp"

using namespace kotlin;

// TODO: Include <UIKit/UIApplication.h> instead when we update clang.
// Trying to include it currently leads to:
// In file included from .../System/Library/Frameworks/UIKit.framework/Headers/UIApplication.h:9:
// In file included from .../System/Library/Frameworks/Foundation.framework/Headers/Foundation.h:12:
// .../System/Library/Frameworks/Foundation.framework/Headers/NSBundle.h:91:143: error: function does not return NSString
// - (NSAttributedString *)localizedAttributedStringForKey:(NSString *)key value:(nullable NSString *)value table:(nullable NSString *)tableName NS_FORMAT_ARGUMENT(1) NS_REFINED_FOR_SWIFT API_AVAILABLE(macos(12.0), ios(15.0), watchos(8.0), tvos(15.0));
//                                                          ~~~~~~~~~~~~~~                                                                       ^                  ~
extern "C" NSNotificationName const UIApplicationDidEnterBackgroundNotification;
extern "C" NSNotificationName const UIApplicationWillEnterForegroundNotification;

class mm::AppStateTracking::Impl : private Pinned {
public:
    explicit Impl(std::function<void(State)> handler) noexcept :
        handler_(std::move(handler)),
        didEnterBackground_(UIApplicationDidEnterBackgroundNotification, [this] { handler_(State::kBackground); }),
        willEnterForeground_(UIApplicationWillEnterForegroundNotification, [this] { handler_(State::kForeground); }) {}

private:
    std::function<void(State)> handler_;
    objc_support::NSNotificationSubscription didEnterBackground_;
    objc_support::NSNotificationSubscription willEnterForeground_;
};

mm::AppStateTracking::AppStateTracking() noexcept {
    switch (compiler::appStateTracking()) {
        case compiler::AppStateTracking::kDisabled:
            break;
        case compiler::AppStateTracking::kEnabled:
            impl_ = std_support::make_unique<Impl>([this](State state) noexcept { setState(state); });
            break;
    }
}

mm::AppStateTracking::~AppStateTracking() = default;

#endif
