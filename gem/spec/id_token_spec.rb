#--
# Cloud Foundry 2012.02.03 Beta
# Copyright (c) [2009-2012] VMware, Inc. All Rights Reserved.
#
# This product is licensed to you under the Apache License, Version 2.0 (the "License").
# You may not use this product except in compliance with the License.
#
# This product includes a number of subcomponents with
# separate copyright notices and license terms. Your use of these
# subcomponents is subject to the terms and conditions of the
# subcomponent's license, as noted in the LICENSE file.
#++

require 'spec_helper'
require 'uaa/id_token'

describe CF::UAA::IdToken do

  subject { CF::UAA::IdToken.new("http://localhost:8080/uaa", "test_app", "test_secret", "read") }

  before :each do
    if !integration_test?
      subject.stub!(:perform_http_request) do |req|
        @input = req
        @response
      end
    end
    subject.trace = true
  end

  it "should do something", :integration=>false do
      subject.trace.should_not be_nil
    end

end
