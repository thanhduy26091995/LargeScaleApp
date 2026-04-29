import Foundation

/// Navigation service contract.
public protocol AppNavigator: AnyObject {
    func navigate(to route: AppRoute)
    func goBack()
}
