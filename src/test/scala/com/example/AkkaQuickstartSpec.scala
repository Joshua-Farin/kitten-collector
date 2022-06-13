//#full-example
package com.example

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike
import com.example.Notifier.Notification
import com.example.KittenShipper.KittenShipment

//#definition
class AkkaQuickstartSpec
    extends ScalaTestWithActorTestKit
    with AnyWordSpecLike {
//#definition

  "A KittenShipper" must {
    // #test
    "notify to notifier" in {
      val replyProbe = createTestProbe[Notification]()
      val underTest = spawn(KittenShipper())
      underTest ! KittenShipment(0, "acorn", true, replyProbe.ref)
      replyProbe.expectMessage(Notification(0, true))
    }
    // #test
  }

}
//#full-example
