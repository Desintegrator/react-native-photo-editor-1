require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name                   = 'RNPhotoEditor'
  s.version                = package['version']
  s.summary                = package['description']
  s.description            = package['description']
  s.homepage               = package['homepage']
  s.license                = package['license']
  s.author                 = package['author']
  s.source                 = { :git => 'ssh://git@bitbucket.pcbltools.ru:7999/class/react-native-photo-editor.git', :tag => s.version }

  s.platform               = :ios, '10.0'
  s.ios.deployment_target  = '10.0'

  s.preserve_paths         = 'LICENSE', 'package.json'
  s.source_files            = "ios/**/*.{h,m,swift}"
  s.exclude_files           = "ios/RNPhotoEditor-demo/**"
  s.resources              = "ios/**/*.{png,jpeg,jpg,storyboard,xib,ttf}"
  s.dependency             'React'
  # s.dependency             'SDWebImage', '~> 5.11.1'
  # s.dependency             'SDWebImageWebPCoder', '~> 0.8.4'
end
